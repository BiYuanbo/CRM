package com.bjpowernode.workbench.service.impl;

import com.bjpowernode.setting.bean.User;
import com.bjpowernode.setting.dao.UserDao;
import com.bjpowernode.util.SqlSessionUtil;
import com.bjpowernode.vo.PaginationVO;
import com.bjpowernode.workbench.bean.Activity;
import com.bjpowernode.workbench.bean.ActivityRemark;
import com.bjpowernode.workbench.dao.ActivityDao;
import com.bjpowernode.workbench.dao.ActivityRemarkDao;
import com.bjpowernode.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {
    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public List<Activity> getActivityListByName(String aname) {
        List<Activity> list = activityDao.getActivityListByName(aname);
        return list;
    }

    @Override
    public List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map) {
        List<Activity> list = activityDao.getActivityListByNameAndNotByClueId(map);
        return list;
    }

    @Override
    public List<Activity> getActivityListByClueId(String clueId) {
        List<Activity> list = activityDao.getActivityListByClueId(clueId);

        return list;
    }

    @Override
    public boolean updateRemark(ActivityRemark ar) {
        boolean flag = true;

        int num = activityRemarkDao.updateRemark(ar);

        if (num!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark ar) {
        boolean flag = true;

        int num = activityRemarkDao.saveRemark(ar);

        if (num!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean deleteRemark(String id) {
        boolean flag = true;

        int num = activityRemarkDao.deleteRemark(id);

        if (num != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public List<ActivityRemark> getRemarkListByAid(String id) {
        List<ActivityRemark> list = activityRemarkDao.getRemarkListByAid(id);
        return list;
    }

    @Override
    public Activity detail(String id) {
        Activity a = activityDao.detail(id);

        return a;
    }

    @Override
    public boolean update(Activity activity) {
        boolean flag = true;

        int count = activityDao.update(activity);

        if (count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
        //取uList
        List<User> ulist = userDao.getUserList();

        //取a
        Activity a = activityDao.getById(id);

        //将uList和a打包到map中
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("ulist",ulist);
        map.put("a",a);

        return map;
    }

    @Override
    public boolean delete(String[] ids) {
        boolean flag = true;
        //查询出需要删除的备注的数量
        int count1 = activityRemarkDao.getCountByAids(ids);

        //删除备注，返回收到影响的条数
        int count2 = activityRemarkDao.deleteByAids(ids);
        if (count1!=count2){
            flag = false;
        }

        int count3 = activityDao.delete(ids);
        if (count3!=ids.length){
            flag=false;
        }

        return flag;
    }

    @Override
    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        //取得total
        Integer total = activityDao.getTotalByCondition(map);

        //取得dataList
        List<Activity> datalist = activityDao.getActivityListByCondition(map);

        //将total和dataList封装到vo
        PaginationVO<Activity> paginationVO = new PaginationVO<Activity>(total,datalist);

        //返回vo
        return paginationVO;
    }

    @Override
    public boolean save(Activity activity) {

        boolean flag = true;

        int count = activityDao.save(activity);

        if (count != 1){
            flag = false;
        }

        return flag;
    }
}
