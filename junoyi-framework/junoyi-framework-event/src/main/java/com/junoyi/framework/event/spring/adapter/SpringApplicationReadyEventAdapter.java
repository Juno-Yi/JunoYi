package com.junoyi.framework.event.spring.adapter;

import com.junoyi.framework.event.core.Event;
import com.junoyi.framework.event.domain.spring.SpringApplicationReadyEvent;
import com.junoyi.framework.event.spring.SpringEventAdapter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;


/**
 * SpringApplicationReadyEventAdapter类用于将Spring的ApplicationReadyEvent事件适配为自定义的事件格式。
 * 该类实现了SpringEventAdapter接口，专门处理应用启动完成事件的转换。
 *
 * @author Fan
 */
@Component
public class SpringApplicationReadyEventAdapter implements SpringEventAdapter<ApplicationReadyEvent> {

    /**
     * 判断当前适配器是否支持指定的Spring事件类型。
     *
     * @param springEvent 待判断的Spring事件对象
     * @return 如果事件是ApplicationReadyEvent类型则返回true，否则返回false
     */
    @Override
    public boolean supports(Object springEvent) {
        return springEvent instanceof ApplicationReadyEvent;
    }

    /**
     * 将Spring的ApplicationReadyEvent事件转换为自定义的SpringApplicationReadyEvent事件。
     *
     * @param springEvent 原始的Spring事件对象
     * @return 转换后的自定义事件对象
     */
    @Override
    public Event adapt(Object springEvent) {
        ApplicationReadyEvent applicationReadyEvent = (ApplicationReadyEvent) springEvent;
        return new SpringApplicationReadyEvent(
                applicationReadyEvent.getSpringApplication(),
                applicationReadyEvent.getArgs(),
                applicationReadyEvent.getApplicationContext(),
                applicationReadyEvent.getTimeTaken()
                );
    }
}
