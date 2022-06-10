package com.bjpowernode.workbench.dao;

import com.bjpowernode.workbench.bean.Customer;

import java.util.List;

public interface CustomerDao {

    Customer getCustomerByName(String company);

    int save(Customer cus);

    List<String> getCustomerName(String name);
}
