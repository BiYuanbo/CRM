package com.bjpowernode.workbench.service;

import com.bjpowernode.vo.PaginationVO;
import com.bjpowernode.workbench.bean.Clue;
import com.bjpowernode.workbench.bean.ClueRemark;
import com.bjpowernode.workbench.bean.Tran;

import java.util.List;
import java.util.Map;

public interface ClueService {
    boolean save(Clue c);

    PaginationVO<Clue> pageList(Map<String, Object> map);

    boolean delete(String[] ids);

    Map<String, Object> getUserListAndClue(String id);

    boolean update(Clue c);

    Clue detail(String id);

    boolean unbund(String id);

    boolean bund(String cid, String[] aids);

    List<ClueRemark> getRemarkListByAid(String id);

    boolean updateRemark(ClueRemark cr);

    boolean saveRemark(ClueRemark cr);

    boolean deleteRemark(String id);

    boolean convert(String clueId, Tran t, String createBy);
}
