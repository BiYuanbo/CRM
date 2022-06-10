package com.bjpowernode.workbench.dao;

import com.bjpowernode.workbench.bean.Activity;
import com.bjpowernode.workbench.bean.Clue;

import java.util.List;
import java.util.Map;

public interface ClueDao {

    int save(Clue c);

    Integer getTotalByCondition(Map<String, Object> map);

    List<Clue> getClueListByCondition(Map<String, Object> map);

    int delete(String[] ids);

    Clue getById(String id);

    int update(Clue c);

    Clue detail(String id);

    int deleteById(String clueId);
}
