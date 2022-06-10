package com.bjpowernode.workbench.dao;

import com.bjpowernode.workbench.bean.Activity;
import com.bjpowernode.workbench.bean.ActivityRemark;

import java.util.List;
import java.util.Map;

public interface ActivityRemarkDao {

    int getCountByAids(String[] ids);

    int deleteByAids(String[] ids);

    List<ActivityRemark> getRemarkListByAid(String id);

    int deleteRemark(String id);

    int saveRemark(ActivityRemark ar);

    int updateRemark(ActivityRemark ar);
}
