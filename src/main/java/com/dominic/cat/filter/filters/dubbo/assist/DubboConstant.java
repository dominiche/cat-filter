package com.dominic.cat.filter.filters.dubbo.assist;

/**
 * Create by dominic on 2018/12/27 16:13.
 */
public interface DubboConstant {

    String CONSUMER_TYPE = "Invoke";
    String CONSUMER_APPLICATION = "Invoke.Application";
    String CONSUMER_SERVER = "Invoke.Server";
    String CONSUMER_TIMEOUT = "Invoke.Timeout";

    String PROVIDER_TYPE = "Service";
    String PROVIDER_APPLICATION = "Service.Application";
    String PROVIDER_SERVER = "Service.Server";


    String DUBBO_PARAM_INVOKER_APP_NAME = "CAT_FILTER_Invoker_Server";
}
