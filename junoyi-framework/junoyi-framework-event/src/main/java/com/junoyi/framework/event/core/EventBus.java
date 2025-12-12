package com.junoyi.framework.event.core;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    private final ExecutorService asyncExecutor;

    private final AtomicInteger threadCounter = new AtomicInteger(0);

    /**
     * 构造方法，初始化异步执行线程池
     * 核心线程数为CPU核心数，最大线程数为核心数的两倍，
     * 使用自定义线程工厂创建带有特定名称和守护状态的线程。
     */
    private EventBus() {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        int maxPoolSize = corePoolSize * 2;
        asyncExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName("EventBus-Async-" + threadCounter.incrementAndGet());
                    thread.setDaemon(true);
                    return thread;
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

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
     * 同步处理器按照优先级从高到低顺序执行
     * 异步处理器提交到线程池并发执行，不阻塞主线程
     *
     * @param event 要触发的事件对象
     * @param <T> 事件类型
     */
    public <T extends Event> void callEvent(T event){
        // 获取该事件类型对应的所有已注册处理器
        List<RegisteredHandler> handlers = registry.getHandlers(event.getClass());
        int handlerCount = handlers.size();
        log.info("EventTrigger", "Event="+event.getClass().getSimpleName() + " | " + "HandlerCount=" + handlerCount );
        // 按优先级顺序遍历并调用每个处理器的方法
        for (RegisteredHandler handler : handlers){
            if (handler.async()) {
                // 异步执行，不阻塞主线程
                asyncExecutor.submit(() -> executeHandler(handler, event));
            } else {
                // 同步执行
                executeHandler(handler, event);
            }
        }
    }

    /**
     * 异步触发事件，强制将所有处理器以异步方式提交到线程池中执行
     * 所有处理器按优先级顺序执行，每个处理器等待前一个完成后再执行
     *
     * @param event 要触发的事件对象
     * @param <T> 事件类型
     */
    public <T extends Event> void callAsyncEvent(T event){
        // 获取该事件类型对应的所有已注册处理器
        List<RegisteredHandler> handlers = registry.getHandlers(event.getClass());
        int listenerCount = handlers.size();
        log.info("AsyncEventTrigger", "Event="+event.getClass().getSimpleName() + " | " + "ListenerCount=" + listenerCount );
        // 所有处理器都异步执行，但按优先级顺序等待完成
        for (RegisteredHandler handler : handlers){
            Future<?> future = asyncExecutor.submit(() -> executeHandler(handler, event));
            try {
                future.get(); // 等待当前处理器完成
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("AsyncEventHandlerInterrupted", "Async handler execution was interrupted: " + handler.method().getName(), e);
            } catch (ExecutionException e) {
                log.error("AsyncEventHandlerExecutionError", "Async handler execution failed: " + handler.method().getName(), e.getCause());
            }
        }
    }

    /**
     * 执行事件处理器
     * 捕获执行过程中的异常，并记录错误日志
     *
     * @param handler 事件处理器
     * @param event 事件对象
     * @param <T> 事件类型
     */
    private <T extends Event> void executeHandler(RegisteredHandler handler, T event) {
        try {
            handler.method().invoke(handler.listener(), event);
        } catch (Exception e){
            log.error("EventHandlerError", "Failed to execute handler: " + handler.method().getName(), e);
        }
    }

    /**
     * 关闭事件总线，释放线程池资源
     * 先尝试平滑关闭线程池，若超时则强制关闭
     */
    public void shutdown() {
        asyncExecutor.shutdown();
        try {
            if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                asyncExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            asyncExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

