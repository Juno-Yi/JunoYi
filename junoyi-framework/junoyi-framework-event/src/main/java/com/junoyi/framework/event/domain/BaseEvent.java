package com.junoyi.framework.event.domain;

import com.junoyi.framework.event.core.Event;

import java.time.Instant;
import java.util.UUID;


/**
 * 事件基类
 * 如果需要自定义各种事件，最少需要实现Event接口类，
 * 这里最佳实践为，自定义的事件类统一继承 BaseEvent 事件基类，
 * 这会使得事件机制更加完善、可拓展
 *
 * @author Fan
 */
public class BaseEvent implements Event {
    /**
     * 事件名称（默认为类名）
     */
    private final String eventName;

    /**
     * 唯一事件ID, 便于日志追踪
     */
    private final String eventId = UUID.randomUUID().toString();

    /**
     * 事件触发时间
     */
    private final Instant timestamp = Instant.now();

    /**
     * 事件来源
     */
    private final Object source;


    /**
     * 无参构造函数，使用类名作为默认事件名称
     */
    public BaseEvent(){
        this.eventName = this.getClass().getSimpleName();
        this.source = this;
    }

    /**
     * 使用指定事件名称初始化事件对象
     * @param eventName 事件名称
     */
    public BaseEvent(String eventName){
        this.eventName = eventName;
        this.source = this;
    }

    /**
     * 使用指定事件源初始化事件对象，默认使用类名作为事件名称
     * @param source 事件来源对象
     */
    public BaseEvent(Object source){
        this.eventName = this.getClass().getSimpleName();
        this.source = source;
    }

    /**
     * 使用指定事件名称和事件源初始化事件对象
     * @param eventName 事件名称
     * @param source 事件来源对象
     */
    public BaseEvent(String eventName, Object source){
        this.eventName = eventName;
        this.source = source;
    }

    /**
     * 获取事件名称
     * @return 返回事件名称字符串
     */
    public String getEventName(){
        return eventName;
    }

    /**
     * 获取事件唯一标识ID
     * @return 返回事件ID字符串
     */
    public String getEventId(){
        return eventId;
    }

    /**
     * 获取事件触发时间戳
     * @return 返回事件发生的时间Instant对象
     */
    public Instant getTimestamp(){
        return timestamp;
    }

    /**
     * 获取事件来源对象
     * @return 返回事件的来源对象
     */
    public Object getSource(){
        return source;
    }

    @Override
    public String toString() {
        return "BaseEvent{" +
                "eventName='" + eventName + '\'' +
                ", eventId='" + eventId + '\'' +
                ", timestamp=" + timestamp +
                ", source=" + source +
                '}';
    }
}
