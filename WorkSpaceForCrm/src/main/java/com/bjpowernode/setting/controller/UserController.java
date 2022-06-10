package com.bjpowernode.setting.controller;

import com.bjpowernode.setting.bean.User;
import com.bjpowernode.setting.service.UserService;
import com.bjpowernode.setting.service.impl.UserServiceImpl;
import com.bjpowernode.util.MD5Util;
import com.bjpowernode.util.PrintJson;
import com.bjpowernode.util.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入用户控制器");
        String path = request.getServletPath();

        if ("/settings/user/login.do".equals(path)){
            login(request,response);
        }else if ("/setting/user/xxx.do".equals(path)){

        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");

        loginPwd = MD5Util.getMD5(loginPwd);
        //接受浏览器的ip地址
        String ip = request.getRemoteAddr();

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        try {
            User user = us.login(loginAct,loginPwd,ip);

            request.getSession().setAttribute("user",user);

            //PrintJson.printJsonFlag(response,true);
            String str = "{\"success\":true}";
            response.getWriter().print(str);
        }catch (Exception e){
            String msg = e.getMessage();

            /*Map<String,Object> map = new HashMap<String, Object>();
            map.put("success",false);
            map.put("msg",msg);
            PrintJson.printJsonObj(response,map);*/
            String str = "{\"success\":false,\"msg\":"+msg+"}";
            System.out.println("helloWorld1");
            response.getWriter().print(str);
        }
    }
}
