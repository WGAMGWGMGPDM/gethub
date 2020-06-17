package com.zr.system.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author: 张忍
 * @Date: 2020-04-14 11:12
 */
@Component
public class AppUtil implements ApplicationContextAware {
    private static ApplicationContext context;

    public static ApplicationContext getContext() {
        return context;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context=applicationContext;
    }
}
