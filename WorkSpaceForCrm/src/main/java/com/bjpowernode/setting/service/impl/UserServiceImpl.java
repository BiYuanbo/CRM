package com.bjpowernode.setting.service.impl;

import com.bjpowernode.ecpection.LoginExpection;
import com.bjpowernode.setting.bean.User;
import com.bjpowernode.setting.dao.UserDao;
import com.bjpowernode.setting.service.UserService;
import com.bjpowernode.util.DateTimeUtil;
import com.bjpowernode.util.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public List<User> getUserList() {
        List<User> list = userDao.getUserList();

        return list;
    }

    @Override
    public User login(String loginAct, String loginPwd, String ip) throws LoginExpection {
        Map<String,String> map = new HashMap<String, String>();

        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);

        User user = userDao.login(map);

        if (user == null){
            throw new LoginExpection("\"账号或密码错误\"");
        }

        //验证失效时间
        String expireTime = user.getExpireTime();
        String currentTime = DateTimeUtil.getSysTime();
        if (expireTime.compareTo(currentTime) < 0){
            throw new LoginExpection("\"账号已失效\"");
        }

        //验证锁定状态
        String lockState = user.getLockState();
        if ("0".equals(lockState)){
            throw new LoginExpection("\"账号已锁定\"");
        }

        //验证ip地址
        String allowIps = user.getAllowIps();
        if (!allowIps.contains(ip)){
            throw new LoginExpection("\"ip地址错误\"");
        }

        System.out.println("helloWorld1");
        return user;
    }
}
