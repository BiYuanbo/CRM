package com.bjpowernode.workbench.dao;

import com.bjpowernode.workbench.bean.Contacts;

import java.util.List;

public interface ContactsDao {

    int save(Contacts con);

    List<Contacts> getContactsListByName(String cname);
}
