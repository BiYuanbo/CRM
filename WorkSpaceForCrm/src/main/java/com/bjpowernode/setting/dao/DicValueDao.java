package com.bjpowernode.setting.dao;

import com.bjpowernode.setting.bean.DicValue;

import java.util.List;

public interface DicValueDao {
    List<DicValue> getListByCode(String code);
}
