package com.junoyi.framework.event.spring.adapter;

import com.junoyi.framework.event.core.Event;
import com.junoyi.framework.event.domain.spring.SpringApplicationStartingEvent;
import com.junoyi.framework.event.spring.SpringEventAdapter;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.stereotype.Component;

/**
 * SpringApplicationStartingEventAdapter类用于将Spring的ApplicationStartingEvent事件适配为自定义的事件格式。
 * 该类实现了SpringEventAdapter接口，专门处理应用启动完成事件的转换。
 * @author Fan
 */
@Deprecated
@Component
public class SpringApplicationStartingEventAdapter implements SpringEventAdapter<ApplicationStartingEvent> {

    /**
     * 判断是否支持指定的Spring事件类型
     *
     * @param springEvent 待判断的Spring事件对象
     * @return 如果事件类型为ApplicationStartingEvent则返回true，否则返回false
     */
    @Override
    public boolean supports(Object springEvent) {
        return springEvent instanceof ApplicationStartingEvent;
    }

    /**
     * 将Spring的ApplicationStartingEvent适配转换为自定义的SpringApplicationStartingEvent
     *
     * @param springEvent 原始的Spring ApplicationStartingEvent事件对象
     * @return 转换后的SpringApplicationStartingEvent事件对象
     */
    @Override
    public Event adapt(Object springEvent) {
        // 类型转换并提取事件信息
        ApplicationStartingEvent applicationStartingEvent = (ApplicationStartingEvent) springEvent;
        // 构造并返回适配后的事件对象
        return new SpringApplicationStartingEvent(
                applicationStartingEvent.getBootstrapContext(),
                applicationStartingEvent.getSpringApplication(),
                applicationStartingEvent.getArgs()
        );
    }
}
