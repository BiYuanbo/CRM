package com.bjpowernode.workbench.service;

import com.bjpowernode.vo.PaginationVO;
import com.bjpowernode.workbench.bean.Tran;
import com.bjpowernode.workbench.bean.TranHistory;

import java.util.List;
import java.util.Map;

public interface TranService {
    PaginationVO<Tran> pageList(Map<String, Object> map);

    boolean save(Tran t, String customerName);

    boolean delete(String[] ids);

    Tran getEditById(String id);

    boolean update(Tran t);

    Tran detail(String id);

    List<TranHistory> getHistoryListByTranId(String id);

    boolean changeStage(Tran t);
}
