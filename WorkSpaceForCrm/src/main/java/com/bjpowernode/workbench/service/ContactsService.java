package com.bjpowernode.workbench.service;

import com.bjpowernode.workbench.bean.Contacts;

import java.util.List;

public interface ContactsService {
    List<Contacts> getContactsListByName(String cname);
}
