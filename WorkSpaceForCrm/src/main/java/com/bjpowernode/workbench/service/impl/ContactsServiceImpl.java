package com.bjpowernode.workbench.service.impl;

import com.bjpowernode.util.SqlSessionUtil;
import com.bjpowernode.workbench.bean.Contacts;
import com.bjpowernode.workbench.dao.ContactsDao;
import com.bjpowernode.workbench.service.ContactsService;

import java.util.List;

public class ContactsServiceImpl implements ContactsService {
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);

    @Override
    public List<Contacts> getContactsListByName(String cname) {
        List<Contacts> list = contactsDao.getContactsListByName(cname);

        return list;
    }
}
