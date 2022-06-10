package com.bjpowernode.workbench.dao;

import com.bjpowernode.workbench.bean.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationDao {

    int unbund(String id);

    int bund(ClueActivityRelation car);

    List<ClueActivityRelation> getListByClueId(String clueId);
}
