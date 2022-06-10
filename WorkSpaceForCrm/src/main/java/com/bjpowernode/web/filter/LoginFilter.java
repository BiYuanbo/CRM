package com.bjpowernode.web.filter;

import com.bjpowernode.setting.bean.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getServletPath();

        //不应该被拦截的资源，自动放行
        if ("/login.jsp".equals(path) || "/settings/user/login.do".equals(path)){
            filterChain.doFilter(request,response);
        }else {
            //其他资源必须验证有没有登陆过
            User user = (User) request.getSession().getAttribute("user");
            if (user != null){
                filterChain.doFilter(request,response);
            }else {
                response.sendRedirect(request.getContextPath()+"/login.jsp");
            }
        }

    }
}
