package com.junoyi.framework.event.config;

import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 事件总线自动配置类
 * 负责在Spring Boot应用关闭时自动释放EventBus的线程池资源
 *
 * @author Fan
 */
@AutoConfiguration
public class EventBusAutoConfiguration {
    private final JunoYiLog log = JunoYiLogFactory.getLogger(EventBusAutoConfiguration.class);

    private final EventBus eventBus = EventBus.get();

    /**
     * 将EventBus注册为Spring Bean
     *
     * @return EventBus单例实例
     */
    @Bean
    public EventBus eventBus() {
        log.info("The event bus has been registered as a Bean !");
        return eventBus;
    }

    /**
     * 在Spring容器销毁时自动调用，释放EventBus的线程池资源
     */
    @PreDestroy
    public void destroy() {
        eventBus.shutdown();
        log.info("Event bus asynchronous thread pool resources have been released!");
    }
}
