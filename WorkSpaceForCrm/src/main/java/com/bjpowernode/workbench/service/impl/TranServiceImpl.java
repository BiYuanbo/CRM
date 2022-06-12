package com.bjpowernode.workbench.service.impl;

import com.bjpowernode.util.DateTimeUtil;
import com.bjpowernode.util.SqlSessionUtil;
import com.bjpowernode.util.UUIDUtil;
import com.bjpowernode.vo.PaginationVO;
import com.bjpowernode.workbench.bean.Customer;
import com.bjpowernode.workbench.bean.Tran;
import com.bjpowernode.workbench.bean.TranHistory;
import com.bjpowernode.workbench.bean.TranRemark;
import com.bjpowernode.workbench.dao.CustomerDao;
import com.bjpowernode.workbench.dao.TranDao;
import com.bjpowernode.workbench.dao.TranHistoryDao;
import com.bjpowernode.workbench.dao.TranRemarkDao;
import com.bjpowernode.workbench.service.TranService;

import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private TranRemarkDao tranRemarkDao = SqlSessionUtil.getSqlSession().getMapper(TranRemarkDao.class);

    @Override
    public boolean saveRemark(TranRemark tRemark) {
        boolean flag = tranRemarkDao.saveRemark(tRemark);
        return flag;
    }

    @Override
    public boolean updateRemark(TranRemark tRemark) {
        boolean flag = true;

        int count = tranRemarkDao.updateRemark(tRemark);
        if (count!=1){
            flag = false;
        }

        return flag;
    }

    @Override
    public boolean deleteRemark(String id) {
        boolean flag = true;
        System.out.println(id);

        int count = tranRemarkDao.deleteRemark("id");
        System.out.println(count);
        if (count != 1){
            flag = false;
        }

        System.out.println(flag);

        return flag;
    }

    @Override
    public List<TranRemark> getRemarkListByTid(String id) {
        List<TranRemark> list = tranRemarkDao.getRemarkListByTid(id);
        return list;
    }

    @Override
    public boolean changeStage(Tran t) {
        boolean flag = true;

        //改变交易阶段
        int count1 = tranDao.changeStage(t);
        if (count1 != 1){
            flag = false;
        }

        //交易阶段改变后，生成一条交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setStage(t.getStage());
        th.setCreateBy(t.getEditBy());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setExpectedDate(t.getExpectedDate());
        th.setMoney(t.getMoney());
        th.setTranId(t.getId());
        //添加交易历史
        int count2 = tranHistoryDao.save(th);
        if (count2 != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public List<TranHistory> getHistoryListByTranId(String id) {
        List<TranHistory> list = tranHistoryDao.getHistoryListByTranId(id);
        return list;
    }

    @Override
    public Tran detail(String id) {

        Tran t = tranDao.detail(id);

        return t;
    }

    @Override
    public boolean update(Tran t) {
        boolean flag = true;

        int num = tranDao.update(t);

        if (num != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Tran getEditById(String id) {

        Tran t = tranDao.getEditById(id);

        return t;
    }

    @Override
    public boolean delete(String[] ids) {
        boolean flag = true;

        //查询出需要删除的历史记录的数量
        int count1 = tranHistoryDao.getCountByTids(ids);

        //删除历史记录，返回收到影响的条数
        int count2 = tranHistoryDao.deleteByTids(ids);
        if (count1 != count2){
            flag = false;
        }

        int count3 = tranDao.delete(ids);
        if (count3 != ids.length){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean save(Tran t, String customerName) {
        boolean flag = true;

        Customer cus = customerDao.getCustomerByName(customerName);
        //如果cus为空，需要创建客户
        if (cus == null){
            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setName(customerName);
            cus.setCreateBy(t.getCreateBy());
            cus.setCreateTime(t.getCreateTime());
            cus.setContactSummary(t.getContactSummary());
            cus.setNextContactTime(t.getNextContactTime());
            cus.setOwner(t.getOwner());

            int count1 = customerDao.save(cus);
            if (count1 != 1){
                flag = false;
            }
        }

        //通过以上对于客户的处理，不论是查询出来已有的客户，还是以前没有，我们新增的客户，总之客户已经有了，客户的id就有了
        //将客户id封装到t对象中
        t.setCustomerId(cus.getId());

        int count2 = tranDao.save(t);
        if (count2 != 1){
            flag = false;
        }

        //添加交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setTranId(t.getId());
        th.setStage(t.getStage());
        th.setMoney(t.getMoney());
        th.setExpectedDate(t.getExpectedDate());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setCreateBy(t.getCreateBy());

        int count3 = tranHistoryDao.save(th);
        if (count3 != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public PaginationVO<Tran> pageList(Map<String, Object> map) {
        //取得total
        Integer total = tranDao.getTotalByCondition(map);

        //取得dataList
        List<Tran> list = tranDao.getTranListByCondition(map);

        //将total和dataList封装到vo
        PaginationVO<Tran> pv = new PaginationVO<Tran>(total,list);

        //返回vo
        return pv;
    }
}

