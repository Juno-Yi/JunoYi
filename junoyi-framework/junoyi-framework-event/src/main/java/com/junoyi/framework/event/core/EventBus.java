package com.junoyi.framework.event.core;

import com.junoyi.framework.event.event.Event;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;

import java.util.List;

/**
 * 事件总线
 *
 * @author Fan
 */
public class EventBus {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(EventBus.class);

    private static final EventBus INSTANCE = new EventBus();

    /**
     * 获取事件总线单例实例
     *
     * @return EventBus单例实例
     */
    public static EventBus get() {
        return INSTANCE;
    }

    private final EventRegistry registry = new EventRegistry();

    private EventBus() {}

    /**
     * 注册事件监听器
     *
     * @param listener 要注册的监听器对象
     */
    public void registerListener(Listener listener){
        registry.registerListener(listener);
    }

    /**
     * 触发事件，调用所有注册的事件处理器
     *
     * @param event 要触发的事件对象
     * @param <T> 事件类型
     */
    public <T extends Event> void callEvent(T event){
        // 获取该事件类型对应的所有已注册处理器
        List<RegisteredHandler> handlers = registry.getHandlers(event.getClass());
        // 遍历并调用每个处理器的方法
        for (RegisteredHandler handler : handlers){
            try {
                handler.method().invoke(handler.listener(), event);
                log.info("事件已经触发");
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }
}
