package com.bjpowernode.workbench.dao;

import com.bjpowernode.workbench.bean.TranHistory;

import java.util.List;


public interface TranHistoryDao {

    int save(TranHistory th);

    int getCountByTids(String[] ids);

    int deleteByTids(String[] ids);

    List<TranHistory> getHistoryListByTranId(String id);
}
