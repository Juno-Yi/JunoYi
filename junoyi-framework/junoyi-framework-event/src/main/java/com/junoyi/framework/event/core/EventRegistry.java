package com.junoyi.framework.event.core;

import com.junoyi.framework.event.annotation.EventHandler;
import com.junoyi.framework.event.event.Event;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件注册器
 *
 * @author Fan
 */
public class EventRegistry {

    private final Map<Class<? extends Event>, List<RegisteredHandler>> handlers = new ConcurrentHashMap<>();

    /**
     * 注册事件监听器
     * 遍历监听器中的所有方法，找到带有@EventHandler注解的方法并注册为事件处理器
     *
     * @param listener 事件监听器实例
     */
    public void registerListener(Listener listener) {
        for (Method method : listener.getClass().getDeclaredMethods()){
            if (!method.isAnnotationPresent(EventHandler.class))
                continue;

            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1 || !Event.class.isAssignableFrom(params[0]))
                throw new RuntimeException("The event listening method must have only one event parameter！");

            @SuppressWarnings("unchecked")
            Class<? extends Event> eventType = (Class<? extends Event>)  params[0];

            EventHandler annotation = method.getAnnotation(EventHandler.class);

            RegisteredHandler handler = new RegisteredHandler(
                    listener,
                    method,
                    annotation.priority()
            );

            handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);

            // 按照优先级排序
            handlers.get(eventType).sort(Comparator.comparingInt(h -> h.priority().getLevel()));

        }
    }

    /**
     * 获取指定事件类型的所有已注册处理器
     *
     * @param eventType 事件类型
     * @return 该事件类型对应的处理器列表，如果不存在则返回空列表
     */
    public List<RegisteredHandler> getHandlers(Class<? extends Event> eventType) {
        return handlers.getOrDefault(eventType, Collections.emptyList());
    }
}
