package com.dominic.cat.filter.helper;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;

/**
 * Create by dominic on 2018/12/28 14:54.
 */
public class DruidDataSourceHelper {

    public static boolean isDruidDataSource(DataSource dataSource) {
        return dataSource instanceof DruidDataSource;
    }

    public static String getDataSourceUrl(DataSource dataSource) {
        return ((DruidDataSource) dataSource).getUrl();
    }
}
