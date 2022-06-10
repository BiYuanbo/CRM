package com.bjpowernode.workbench.dao;

import com.bjpowernode.workbench.bean.ClueRemark;

import java.util.List;

public interface ClueRemarkDao {

    int getCountByAids(String[] ids);

    int deleteByAids(String[] ids);

    List<ClueRemark> getRemarkListByAid(String id);

    int updateRemark(ClueRemark cr);

    int saveRemark(ClueRemark cr);

    int deleteRemark(String id);
}
