package com.bjpowernode.setting.service;

import com.bjpowernode.ecpection.LoginExpection;
import com.bjpowernode.setting.bean.User;

import java.util.List;

public interface UserService {

    User login(String loginAct, String loginPwd, String ip) throws LoginExpection;

    List<User> getUserList();
}
