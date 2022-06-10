package com.bjpowernode.setting.dao;

import com.bjpowernode.setting.bean.User;

import java.util.List;
import java.util.Map;

public interface UserDao {

    User login(Map<String, String> map);

    List<User> getUserList();
;
}
