package com.dominic.cat.filter.config;

import com.dominic.cat.filter.helper.CatMybatisFilterConfigHelper;
import com.dominic.cat.filter.utils.ClassUtil;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class CatFilterListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent applicationEvent) {
        if (applicationEvent.getSource() instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory = (ListableBeanFactory) applicationEvent.getSource();

            //mybatis
            if (ClassUtil.isPresent("org.apache.ibatis.session.SqlSessionFactory")) {
                CatMybatisFilterConfigHelper.configCatMybatisPlugin(listableBeanFactory);
            }
        }
    }
}
