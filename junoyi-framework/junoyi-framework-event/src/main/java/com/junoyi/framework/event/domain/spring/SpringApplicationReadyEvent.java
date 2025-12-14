package com.junoyi.framework.event.domain.spring;


import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;


/**
 * Spring 中 ApplicationReadyEvent
 *
 * @author Fan
 */
public class SpringApplicationReadyEvent extends SpringApplicationEvent {

    private final ConfigurableApplicationContext context;

    private final Duration timeTaken;


    /**
     * 构造方法，初始化 SpringApplicationReadyEvent 实例
     *
     * @param application SpringApplication应用实例
     * @param args 启动参数数组
     * @param context 应用程序上下文对象
     * @param timeTaken 应用启动耗时
     */
    public SpringApplicationReadyEvent(SpringApplication application, String[] args, ConfigurableApplicationContext context, Duration timeTaken){
        super(application,args);
        this.context = context;
        this.timeTaken = timeTaken;
    }

    /**
     * 获取应用程序上下文对象
     *
     * @return ConfigurableApplicationContext 应用程序上下文实例
     */
    public ConfigurableApplicationContext getApplicationContext(){
        return context;
    }

    /**
     * 获取应用启动耗时
     *
     * @return Duration 启动耗时时间间隔
     */
    public Duration getTimeTaken(){
        return timeTaken;
    }

}
