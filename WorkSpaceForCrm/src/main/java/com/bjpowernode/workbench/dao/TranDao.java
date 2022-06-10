package com.bjpowernode.workbench.dao;

import com.bjpowernode.workbench.bean.Tran;

import java.util.List;
import java.util.Map;

public interface TranDao {

    int save(Tran t);

    Integer getTotalByCondition(Map<String, Object> map);

    List<Tran> getTranListByCondition(Map<String, Object> map);

    int delete(String[] ids);

    Tran getEditById(String id);

    int update(Tran t);

    Tran detail(String id);

    int changeStage(Tran t);
}
