package com.bjpowernode.workbench.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.setting.bean.User;
import com.bjpowernode.setting.service.UserService;
import com.bjpowernode.setting.service.impl.UserServiceImpl;
import com.bjpowernode.util.DateTimeUtil;
import com.bjpowernode.util.ServiceFactory;
import com.bjpowernode.util.SqlSessionUtil;
import com.bjpowernode.util.UUIDUtil;
import com.bjpowernode.vo.PaginationVO;
import com.bjpowernode.workbench.bean.Activity;
import com.bjpowernode.workbench.bean.Contacts;
import com.bjpowernode.workbench.bean.Tran;
import com.bjpowernode.workbench.bean.TranHistory;
import com.bjpowernode.workbench.service.*;
import com.bjpowernode.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.workbench.service.impl.ContactsServiceImpl;
import com.bjpowernode.workbench.service.impl.CustomerServiceImpl;
import com.bjpowernode.workbench.service.impl.TranServiceImpl;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入交易控制器");
        String path = request.getServletPath();

        if ("/workbench/tran/pageList.do".equals(path)) {
            pageList(request,response);
        } else if ("/workbench/tran/add.do".equals(path)) {
            add(request,response);
        } else if ("/workbench/tran/getCustomerName.do".equals(path)) {
            getCustomerName(request,response);
        } else if ("/workbench/tran/getActivityListByName.do".equals(path)) {
            getActivityListByName(request,response);
        } else if ("/workbench/tran/getContactsListByName.do".equals(path)) {
            getContactsListByName(request,response);
        } else if ("/workbench/tran/save.do".equals(path)) {
            save(request,response);
        } else if ("/workbench/tran/delete.do".equals(path)) {
            delete(request,response);
        } else if ("/workbench/tran/edit.do".equals(path)) {
            edit(request,response);
        } else if ("/workbench/tran/update.do".equals(path)) {
            update(request,response);
        } else if ("/workbench/tran/detail.do".equals(path)) {
            detail(request,response);
        } else if ("/workbench/tran/getHistoryListByTranId.do".equals(path)) {
            getHistoryListByTranId(request,response);
        } else if ("/workbench/tran/changeStage.do".equals(path)) {
            changeStage(request,response);
        }
    }

    //改变阶段的操作
    private void changeStage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());

        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");

        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editTime = DateTimeUtil.getSysTime();

        Tran t = new Tran();
        t.setId(id);
        t.setStage(stage);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setEditBy(editBy);
        t.setEditTime(editTime);

        boolean flag = tranService.changeStage(t);

        Map<String,String> pMap = (Map<String, String>) request.getServletContext().getAttribute("pMap");
        t.setPossibility(pMap.get(stage));

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",flag);
        map.put("t",t);

        JSONObject json = new JSONObject(map);
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(json);
        pw.flush();
        pw.close();
    }

    //根据交易id取得相应的历史列表
    private void getHistoryListByTranId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());

        String id = request.getParameter("tranId");

        List<TranHistory> list = tranService.getHistoryListByTranId(id);

        Map<String,String> pMap = (Map<String, String>) request.getServletContext().getAttribute("pMap");

        for (TranHistory tranHistory : list) {
            String stage = tranHistory.getStage();
            String possibility = pMap.get(stage);
            tranHistory.setPossibility(possibility);
        }

        String json  = JSON.toJSONString(list);

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(json);
        pw.flush();
        pw.close();
    }

    //跳转到详情信息页
    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());

        String id = request.getParameter("id");

        Tran t = tranService.detail(id);

        //处理可能性
        String stage = t.getStage();
        ServletContext application = request.getServletContext();
        Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");
        String possibility = pMap.get(stage);

        t.setPossibility(possibility);

        request.setAttribute("t",t);

        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);
    }

    //执行更新操作
    private void update(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());

        String id = request.getParameter("id");
        //String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        //String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        /*String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");*/
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        Tran t = new Tran();
        t.setId(id);
        /*t.setOwner(owner);*/
        t.setMoney(money);
        t.setName(name);
        t.setExpectedDate(expectedDate);

        t.setStage(stage);
        t.setType(type);
        t.setSource(source);
        /*t.setActivityId(activityId);
        t.setContactsId(contactsId);*/
        t.setEditBy(editBy);
        t.setEditTime(editTime);
        t.setDescription(description);
        t.setContactSummary(contactSummary);
        t.setNextContactTime(nextContactTime);

        boolean flag = tranService.update(t);

        if (flag){
            response.sendRedirect(request.getContextPath()+"/workbench/transaction/index.jsp");
        }
    }

    //跳转到编辑页面
    private void edit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());

        String id = request.getParameter("id");

        Tran t = tranService.getEditById(id);

        request.setAttribute("t",t);

        request.getRequestDispatcher("/workbench/transaction/edit.jsp").forward(request,response);
    }

    //删除交易操作
    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());

        String[] ids = request.getParameterValues("id");

        boolean flag = tranService.delete(ids);

        if (flag == true){
            String str = "{\"success\":true}";
            response.getWriter().print(str);
        }else {
            String str = "{\"success\":false}";
            response.getWriter().print(str);
        }
    }

    //执行添加交易的操作
    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        Tran t = new Tran();
        t.setId(id);
        t.setOwner(owner);
        t.setMoney(money);
        t.setName(name);
        t.setExpectedDate(expectedDate);

        t.setStage(stage);
        t.setType(type);
        t.setSource(source);
        t.setActivityId(activityId);
        t.setContactsId(contactsId);
        t.setCreateTime(createTime);
        t.setCreateBy(createBy);
        t.setDescription(description);
        t.setContactSummary(contactSummary);
        t.setNextContactTime(nextContactTime);

        boolean flag = tranService.save(t,customerName);

        if (flag){
            response.sendRedirect(request.getContextPath()+"/workbench/transaction/index.jsp");
        }
    }

    //根据名称模糊查询联系人名称列表
    private void getContactsListByName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ContactsService contactsService = (ContactsService) ServiceFactory.getService(new ContactsServiceImpl());

        String cname = request.getParameter("cname");

        List<Contacts> list = contactsService.getContactsListByName(cname);

        String json  = JSON.toJSONString(list);

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(json);
        pw.flush();
        pw.close();
    }

    //根据名称模糊查询市场活动列表
    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        String aname = request.getParameter("aname");

        List<Activity> list = activityService.getActivityListByName(aname);

        String json  = JSON.toJSONString(list);

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(json);
        pw.flush();
        pw.close();
    }

    //取得客户名称列表，按照客户名称模糊查询
    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        List<String> list = cs.getCustomerName(name);

        String json  = JSON.toJSONString(list);

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(json);
        pw.flush();
        pw.close();
    }

    //进入到交易添加页的操作
    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> list = userService.getUserList();

        request.setAttribute("list",list);

        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);
    }

    //展示交易列表
    private void pageList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());

        String pageNoStr = request.getParameter("pageNo");
        int pageNo = Integer.valueOf(pageNoStr);
        String pageSizeStr = request.getParameter("pageSize");
        //每页展现的记录数
        int pageSize = Integer.valueOf(pageSizeStr);
        //计算出略过的记录数
        int skipCount = (pageNo-1)*pageSize;

        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String conName = request.getParameter("conName");
        String cusName = request.getParameter("cusName");

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);
        map.put("name",name);
        map.put("owner",owner);
        map.put("conName",conName);
        map.put("cusName",cusName);

        PaginationVO<Tran> pv = tranService.pageList(map);

        String str = JSONObject.toJSONString(pv);
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(str);
        pw.flush();
        pw.close();
    }
}
