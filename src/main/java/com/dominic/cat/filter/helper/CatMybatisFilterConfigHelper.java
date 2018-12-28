package com.dominic.cat.filter.helper;

import com.dominic.cat.filter.filters.mybatis.CatMybatisPlugin;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.ListableBeanFactory;

/**
 * Create by dominic on 2018/12/26 15:20.
 */
public class CatMybatisFilterConfigHelper {
    public static void configCatMybatisPlugin(ListableBeanFactory listableBeanFactory) {
        String[] factories = listableBeanFactory.getBeanNamesForType(SqlSessionFactory.class);
        for (String factoryName : factories) {
            SqlSessionFactory sessionFactory = (SqlSessionFactory) listableBeanFactory.getBean(factoryName);
            Configuration configuration = sessionFactory.getConfiguration();
            configuration.addInterceptor(new CatMybatisPlugin());
        }
    }
}
