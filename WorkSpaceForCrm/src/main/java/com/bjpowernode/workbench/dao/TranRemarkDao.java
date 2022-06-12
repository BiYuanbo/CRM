package com.bjpowernode.workbench.dao;

import com.bjpowernode.workbench.bean.TranRemark;

import java.util.List;

public interface TranRemarkDao {
    List<TranRemark> getRemarkListByTid(String id);

    int deleteRemark(String id);

    int updateRemark(TranRemark tRemark);

    boolean saveRemark(TranRemark tRemark);
}
