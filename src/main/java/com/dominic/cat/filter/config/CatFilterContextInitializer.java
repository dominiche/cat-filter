package com.dominic.cat.filter.config;

import com.dominic.cat.filter.filters.springMVC.SpringMVCFilter;
import com.dominic.cat.filter.utils.ClassUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Create by dominic on 2018/12/26 16:36.
 */
@Component
public class CatFilterContextInitializer implements ApplicationContextAware, ApplicationContextInitializer<ConfigurableApplicationContext> {
    private boolean springMVCConfigured = false;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        //ApplicationContextInitializer: spring boot refresh() 前调用
        //1.1 for spring boot项目
        configSpringMVCCatFilter(applicationContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //1.2 for 普通spring项目
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
            configSpringMVCCatFilter(configurableApplicationContext);
        }

    }

    private void configSpringMVCCatFilter(ConfigurableApplicationContext applicationContext) {
        if (!springMVCConfigured && ClassUtil.isPresent("org.springframework.web.servlet.config.annotation.WebMvcConfigurer")) {
            BeanDefinitionRegistry definitionRegistry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
            Class<SpringMVCFilter> filterClass = SpringMVCFilter.class;
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(filterClass);
            definitionRegistry.registerBeanDefinition(filterClass.getName(), beanDefinitionBuilder.getRawBeanDefinition());
            springMVCConfigured = true;
        }
    }
}
