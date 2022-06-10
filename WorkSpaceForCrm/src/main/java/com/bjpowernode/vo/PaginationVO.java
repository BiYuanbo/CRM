package com.bjpowernode.vo;

import java.util.List;

public class PaginationVO<T> {
    private Integer total;
    private List<T> dataList;

    public PaginationVO() {
    }

    public PaginationVO(Integer total, List<T> dataList) {
        this.total = total;
        this.dataList = dataList;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    @Override
    public String toString() {
        return "PaginationVO{" +
                "total=" + total +
                ", dataList=" + dataList +
                '}';
    }
}
