package com.bjpowernode.web.listener;

import com.bjpowernode.setting.bean.DicValue;
import com.bjpowernode.setting.service.DicService;
import com.bjpowernode.setting.service.impl.DicServiceImpl;
import com.bjpowernode.util.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;


public class SysInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("服务器缓存处理数据字典开始");

        ServletContext application = sce.getServletContext();

        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());

        Map<String, List<DicValue>> map = ds.getAll();

        Set<String> set = map.keySet();

        for (String key : set){
            application.setAttribute(key,map.get(key));
        }

        System.out.println("服务器缓存处理数据字典结束");

        //数据字典处理完毕后，处理Stage2Possibility.properties文件
        Map<String,String> pMap = new HashMap<String, String>();

        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");

        Enumeration<String> keys = rb.getKeys();

        while (keys.hasMoreElements()){
            //阶段
            String key = keys.nextElement();
            //可能性
            String value = rb.getString(key);

            pMap.put(key,value);
        }

        //将pMap保存到服务器缓存中
        application.setAttribute("pMap",pMap);
    }
}
