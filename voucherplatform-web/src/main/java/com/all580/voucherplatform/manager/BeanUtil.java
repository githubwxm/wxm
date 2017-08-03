package com.all580.voucherplatform.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wxming on 2016/10/28 0028.
 */

@Slf4j
public class BeanUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static String defaultApplicationContext = "classpath:applicationContext*.xml";
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        if(applicationContext != null) {
            throw new IllegalStateException("ApplicationContextHolder already holded 'applicationContext'.");
        }
        applicationContext = context;
        log.info("holded applicationContext,displayName:"+applicationContext.getDisplayName());
    }

    public static ApplicationContext getApplicationContext() {
        if(applicationContext == null){
            applicationContext = new ClassPathXmlApplicationContext(defaultApplicationContext);
        }
        if(applicationContext == null)
            throw new IllegalStateException("'applicationContext' property is null,ApplicationContextHolder not yet init.");
        return applicationContext;
    }

    public static Object getBean(String beanName) {
        return getApplicationContext().getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> beanClass){
        return getApplicationContext().getBean(beanName, beanClass);
    }

    public static void cleanHolder() {
        applicationContext = null;
    }

    public void setDefaultApplicationContext(String defaultApplicationContext) {
        BeanUtil.defaultApplicationContext = defaultApplicationContext;
    }
}
