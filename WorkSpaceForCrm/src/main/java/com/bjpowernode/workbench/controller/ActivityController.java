package com.bjpowernode.workbench.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.setting.bean.User;
import com.bjpowernode.setting.service.UserService;
import com.bjpowernode.setting.service.impl.UserServiceImpl;
import com.bjpowernode.util.DateTimeUtil;
import com.bjpowernode.util.MD5Util;
import com.bjpowernode.util.ServiceFactory;
import com.bjpowernode.util.UUIDUtil;
import com.bjpowernode.vo.PaginationVO;
import com.bjpowernode.workbench.bean.Activity;
import com.bjpowernode.workbench.bean.ActivityRemark;
import com.bjpowernode.workbench.dao.ActivityDao;
import com.bjpowernode.workbench.dao.ActivityRemarkDao;
import com.bjpowernode.workbench.service.ActivityService;
import com.bjpowernode.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController extends HttpServlet {
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入市场活动控制器");
        String path = request.getServletPath();

        if ("/workbench/activity/getUserList.do".equals(path)) {
            getUserList(request,response);
        } else if ("/workbench/activity/save.do".equals(path)) {
            save(request,response);
        } else if ("/workbench/activity/pageList.do".equals(path)) {
            pageList(request,response);
        } else if ("/workbench/activity/delete.do".equals(path)) {
            delete(request,response);
        } else if ("/workbench/activity/getUserListAndActivity.do".equals(path)) {
            getUserListAndActivity(request,response);
        } else if ("/workbench/activity/update.do".equals(path)) {
            update(request,response);
        } else if ("/workbench/activity/detail.do".equals(path)) {
            detail(request,response);
        } else if ("/workbench/activity/getRemarkListByAid.do".equals(path)) {
            getRemarkListByAid(request,response);
        } else if ("/workbench/activity/deleteRemark.do".equals(path)) {
            deleteRemark(request,response);
        } else if ("/workbench/activity/saveRemark.do".equals(path)) {
            saveRemark(request,response);
        } else if ("/workbench/activity/updateRemark.do".equals(path)) {
            updateRemark(request,response);
        }
    }

    //更新备注
    private void updateRemark(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");

        //更新时间，当前系统时间
        String editTime = DateTimeUtil.getSysTime();
        //更新人：当前登录用户
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "1";

        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setEditBy(editBy);
        ar.setEditTime(editTime);
        ar.setEditFlag(editFlag);

        boolean flag = activityService.updateRemark(ar);

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",flag);
        map.put("ar",ar);

        String str = JSONObject.toJSONString(map);
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(str);
        pw.flush();
        pw.close();
    }

    //添加备注
    private void saveRemark(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        String noteContent = request.getParameter("noteContent");
        String activityId = request.getParameter("activityId");

        //创建时间，当前系统时间
        String createTime = DateTimeUtil.getSysTime();
        //创建人：当前登录用户
        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        ActivityRemark ar = new ActivityRemark();
        ar.setId(UUIDUtil.getUUID());
        ar.setNoteContent(noteContent);
        ar.setActivityId(activityId);
        ar.setCreateBy(createBy);
        ar.setCreateTime(createTime);
        ar.setEditFlag("0");

        boolean flag = activityService.saveRemark(ar);

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",flag);
        map.put("ar",ar);

        String str = JSONObject.toJSONString(map);
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(str);
        pw.flush();
        pw.close();
    }

    //删除备注
    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        String id = request.getParameter("id");

        boolean flag = activityService.deleteRemark(id);

        if (flag == true){
            String str = "{\"success\":true}";
            response.getWriter().print(str);
        }else {
            String str = "{\"success\":false}";
            response.getWriter().print(str);
        }
    }

    //通过id获取备注信息
    private void getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        String id = request.getParameter("activityId");

        List<ActivityRemark> list = activityService.getRemarkListByAid(id);

        String json  = JSON.toJSONString(list);

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(json);
        pw.flush();
        pw.close();
    }

    //跳转到详情信息页
    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        String id = request.getParameter("id");

        Activity a = activityService.detail(id);

        request.setAttribute("a",a);

        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);
    }

    //更新用户信息
    private void update(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");

        //修改时间，当前系统时间
        String editTime = DateTimeUtil.getSysTime();
        //修改人：当前登录用户
        String editBy = ((User)request.getSession().getAttribute("user")).getName();

        Activity activity = new Activity(id,owner,name,startDate,endDate,cost,description,null,null,editTime,editBy);

        boolean flag = activityService.update(activity);

        String str = "{\"success\":"+flag+"}";
        response.getWriter().print(str);
    }

    //获取用户列表和市场活动
    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        String id = request.getParameter("id");

        Map<String,Object> map = activityService.getUserListAndActivity(id);

        JSONObject json = new JSONObject(map);
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(json);
        pw.flush();
        pw.close();
    }

    //删除用户信息
    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        String ids[] = request.getParameterValues("id");

        boolean flag = activityService.delete(ids);

        if (flag == true){
            String str = "{\"success\":true}";
            response.getWriter().print(str);
        }else {
            String str = "{\"success\":false}";
            response.getWriter().print(str);
        }
    }

    //查询分页信息
    private void pageList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        String pageNoStr = request.getParameter("pageNo");
        int pageNo = Integer.valueOf(pageNoStr);
        String pageSizeStr = request.getParameter("pageSize");
        //每页展现的记录数
        int pageSize = Integer.valueOf(pageSizeStr);
        //计算出略过的记录数
        int skipCount = (pageNo-1)*pageSize;

        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);

        PaginationVO<Activity> pv = activityService.pageList(map);

        String str = JSONObject.toJSONString(pv);
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(str);
        pw.flush();
        pw.close();
    }

    //添加用户信息
    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");

        //创建时间，当前系统时间
        String createTime = DateTimeUtil.getSysTime();
        //创建人：当前登录用户
        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        Activity activity = new Activity(id,owner,name,startDate,endDate,cost,description,createTime,createBy,null,null);

        boolean flag = activityService.save(activity);

        String str = "{\"success\":"+flag+"}";
        response.getWriter().print(str);
    }

    //取得用户信息列表
    private void getUserList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> list = us.getUserList();

        String json  = JSON.toJSONString(list);

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(json);
        pw.flush();
        pw.close();
    }

}
