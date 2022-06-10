package com.bjpowernode.workbench.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.setting.bean.User;
import com.bjpowernode.setting.service.UserService;
import com.bjpowernode.setting.service.impl.UserServiceImpl;
import com.bjpowernode.util.DateTimeUtil;
import com.bjpowernode.util.ServiceFactory;
import com.bjpowernode.util.UUIDUtil;
import com.bjpowernode.vo.PaginationVO;
import com.bjpowernode.workbench.bean.*;
import com.bjpowernode.workbench.service.ActivityService;
import com.bjpowernode.workbench.service.ClueService;
import com.bjpowernode.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.workbench.service.impl.ClueServiceImpl;

import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClueController extends HttpServlet {
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入线索控制器");
        String path = request.getServletPath();

        if ("/workbench/clue/getUserList.do".equals(path)) {
            getUserList(request,response);
        } else if ("/workbench/clue/save.do".equals(path)) {
            save(request,response);
        } else if ("/workbench/clue/pageList.do".equals(path)) {
            pageList(request,response);
        } else if ("/workbench/clue/delete.do".equals(path)) {
            delete(request,response);
        } else if ("/workbench/clue/getUserListAndClue.do".equals(path)) {
            getUserListAndClue(request,response);
        } else if ("/workbench/clue/update.do".equals(path)) {
            update(request,response);
        } else if ("/workbench/clue/detail.do".equals(path)) {
            detail(request,response);
        } else if ("/workbench/clue/getActivityListByClueId.do".equals(path)) {
            getActivityListByClueId(request,response);
        } else if ("/workbench/clue/unbund.do".equals(path)) {
            unbund(request,response);
        } else if ("/workbench/clue/getActivityListByNameAndNotByClueId.do".equals(path)) {
            getActivityListByNameAndNotByClueId(request,response);
        } else if ("/workbench/clue/bund.do".equals(path)) {
            bund(request,response);
        } else if ("/workbench/clue/getRemarkListByAid.do".equals(path)) {
            getRemarkListByAid(request,response);
        } else if ("/workbench/clue/updateRemark.do".equals(path)) {
            updateRemark(request,response);
        } else if ("/workbench/clue/saveRemark.do".equals(path)) {
            saveRemark(request,response);
        } else if ("/workbench/clue/deleteRemark.do".equals(path)) {
            deleteRemark(request,response);
        } else if ("/workbench/clue/getActivityListByName.do".equals(path)) {
            getActivityListByName(request,response);
        } else if ("/workbench/clue/convert.do".equals(path)) {
            convert(request,response);
        }

    }

    //执行线索转换操作
    private void convert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        String clueId = request.getParameter("clueId");

        //接受是否需要创建交易的标记
        String flag = request.getParameter("flag");

        Tran t = null;
        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        if ("a".equals(flag)){
            //接受交易表单中的参数
            String money = request.getParameter("money");
            String name = request.getParameter("name");
            String expectedDate = request.getParameter("expectedDate");
            String stage = request.getParameter("stage");
            String activityId = request.getParameter("activityId");
            String id = UUIDUtil.getUUID();
            String createTime = DateTimeUtil.getSysTime();

            t = new Tran();
            t.setMoney(money);
            t.setName(name);
            t.setExpectedDate(expectedDate);
            t.setStage(stage);
            t.setActivityId(activityId);
            t.setId(id);
            t.setCreateBy(createBy);
            t.setCreateTime(createTime);
        }

        boolean flag1 = clueService.convert(clueId,t,createBy);

        if (flag1){
            response.sendRedirect(request.getContextPath()+"/workbench/clue/index.jsp");
        }
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

    //删除备注列表
    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        String id = request.getParameter("id");

        boolean flag = clueService.deleteRemark(id);

        if (flag == true){
            String str = "{\"success\":true}";
            response.getWriter().print(str);
        }else {
            String str = "{\"success\":false}";
            response.getWriter().print(str);
        }
    }

    //添加备注列表
    private void saveRemark(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        String noteContent = request.getParameter("noteContent");
        String clueId = request.getParameter("clueId");

        //创建时间，当前系统时间
        String createTime = DateTimeUtil.getSysTime();
        //创建人：当前登录用户
        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        ClueRemark cr = new ClueRemark();
        cr.setId(UUIDUtil.getUUID());
        cr.setCreateBy(createBy);
        cr.setCreateTime(createTime);
        cr.setNoteContent(noteContent);
        cr.setClueId(clueId);
        cr.setEditFlag("0");

        boolean flag = clueService.saveRemark(cr);

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",flag);
        map.put("cr",cr);

        String str = JSONObject.toJSONString(map);
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(str);
        pw.flush();
        pw.close();
    }

    //更新备注列表
    private void updateRemark(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");

        //更新时间，当前系统时间
        String editTime = DateTimeUtil.getSysTime();
        //更新人：当前登录用户
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "1";

        ClueRemark cr = new ClueRemark();
        cr.setId(id);
        cr.setNoteContent(noteContent);
        cr.setEditBy(editBy);
        cr.setEditTime(editTime);
        cr.setEditFlag(editFlag);

        boolean flag = clueService.updateRemark(cr);

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",flag);
        map.put("cr",cr);

        String str = JSONObject.toJSONString(map);
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(str);
        pw.flush();
        pw.close();
    }

    //查看备注列表
    private void getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        String id = request.getParameter("clueId");

        List<ClueRemark> list = clueService.getRemarkListByAid(id);

        String json  = JSON.toJSONString(list);

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(json);
        pw.flush();
        pw.close();
    }

    //关联市场活动
    private void bund(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        String cid = request.getParameter("cid");
        String[] aids = request.getParameterValues("aid");

        boolean flag = clueService.bund(cid,aids);

        String str = "{\"success\":"+flag+"}";
        response.getWriter().print(str);
    }

    //查询市场活动列表（根据名称模糊查+排除掉以及关联的线索）
    private void getActivityListByNameAndNotByClueId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        String aname = request.getParameter("aname");
        String clueId = request.getParameter("clueId");

        Map<String,String> map = new HashMap<String, String>();
        map.put("aname",aname);
        map.put("clueId",clueId);

        List<Activity> list = activityService.getActivityListByNameAndNotByClueId(map);

        String json  = JSON.toJSONString(list);

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(json);
        pw.flush();
        pw.close();
    }

    //解除关联操作
    private void unbund(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        String id = request.getParameter("id");

        boolean flag = clueService.unbund(id);

        String str = "{\"success\":"+flag+"}";
        response.getWriter().print(str);
    }

    //通过线索id获得市场活动列表
    private void getActivityListByClueId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String clueId = request.getParameter("clueId");

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<Activity> list = activityService.getActivityListByClueId(clueId);

        String json  = JSON.toJSONString(list);

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(json);
        pw.flush();
        pw.close();
    }

    //跳转到详情信息页
    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        String id = request.getParameter("id");

        Clue c = clueService.detail(id);

        request.setAttribute("c",c);

        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request,response);
    }

    //更新线索信息
    private void update(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        Clue c = new Clue();
        c.setId(id);
        c.setFullname(fullname);
        c.setAppellation(appellation);
        c.setOwner(owner);
        c.setCompany(company);
        c.setJob(job);
        c.setEmail(email);
        c.setPhone(phone);
        c.setWebsite(website);
        c.setMphone(mphone);
        c.setState(state);
        c.setSource(source);
        c.setEditBy(editBy);
        c.setEditTime(editTime);
        c.setDescription(description);
        c.setContactSummary(contactSummary);
        c.setNextContactTime(nextContactTime);
        c.setAddress(address);

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = clueService.update(c);

        String str = "{\"success\":"+flag+"}";
        response.getWriter().print(str);
    }

    //得到用户列表和线索信息
    private void getUserListAndClue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        String id = request.getParameter("id");

        Map<String,Object> map = clueService.getUserListAndClue(id);

        JSONObject json = new JSONObject(map);
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(json);
        pw.flush();
        pw.close();
    }

    //删除线索信息
    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        String ids[] = request.getParameterValues("id");

        boolean flag = clueService.delete(ids);

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
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        String pageNoStr = request.getParameter("pageNo");
        int pageNo = Integer.valueOf(pageNoStr);
        String pageSizeStr = request.getParameter("pageSize");
        //每页展现的记录数
        int pageSize = Integer.valueOf(pageSizeStr);
        //计算出略过的记录数
        int skipCount = (pageNo-1)*pageSize;

        String fullname = request.getParameter("fullname");
        String company = request.getParameter("company");
        String cpyPhone = request.getParameter("cpyPhone");
        String owner = request.getParameter("owner");
        String phone = request.getParameter("phone");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);
        map.put("fullname",fullname);
        map.put("company",company);
        map.put("cpyPhone",cpyPhone);
        map.put("owner",owner);
        map.put("phone",phone);

        PaginationVO<Clue> pv = clueService.pageList(map);

        String str = JSONObject.toJSONString(pv);
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(str);
        pw.flush();
        pw.close();
    }

    //添加线索信息
    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = UUIDUtil.getUUID();
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        Clue c = new Clue();
        c.setId(id);
        c.setFullname(fullname);
        c.setAppellation(appellation);
        c.setOwner(owner);
        c.setCompany(company);
        c.setJob(job);
        c.setEmail(email);
        c.setPhone(phone);
        c.setWebsite(website);
        c.setMphone(mphone);
        c.setState(state);
        c.setSource(source);
        c.setCreateBy(createBy);
        c.setCreateTime(createTime);
        c.setDescription(description);
        c.setContactSummary(contactSummary);
        c.setNextContactTime(nextContactTime);
        c.setAddress(address);

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = clueService.save(c);

        String str = "{\"success\":"+flag+"}";
        response.getWriter().print(str);
    }

    //获取线索列表
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
