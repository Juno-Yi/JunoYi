package com.junoyi.demo.listener;

import com.junoyi.demo.event.TestEvent;
import com.junoyi.framework.event.annotation.EventHandler;
import com.junoyi.framework.event.annotation.EventListener;
import com.junoyi.framework.event.domain.spring.SpringApplicationReadyEvent;
import com.junoyi.framework.event.domain.spring.SpringApplicationStartingEvent;
import com.junoyi.framework.event.enums.EventPriority;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;

/**
 * Test事件监听器
 */
@EventListener
public class TestEventListener {
    private final JunoYiLog log = JunoYiLogFactory.getLogger(TestEventListener.class);

    @EventHandler
    public void onSpringApplicationStartingEvent(SpringApplicationStartingEvent event){
        System.out.println("===============");
        System.out.println(" 测试  SpringApplicationStartingEvent 事件监听");
        System.out.println("===============");
    }

    /**
     * SpringApplicationReadyEvent被触发时候
     * @param event SpringApplicationStartedEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onSpringApplicationReadyEvent(SpringApplicationReadyEvent event){
        System.out.println("===============");
        System.out.println(" 测试  SpringApplicationReadyEvent 事件监听");
        System.out.println("===============");
    }

    /**
     * 事件监听器
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onTestEvent(TestEvent event){

        System.out.println("优先级：高");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTest2Event(TestEvent event){

        System.out.println("优先级：监控（最高的）");
    }

    @EventHandler
    public void onTest3Event(TestEvent event){

        System.out.println("优先级：默认");
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onTest4Event(TestEvent event){

        System.out.println("优先级：低");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTest6Event(TestEvent event){

        System.out.println("优先级：最低");
    }

    @EventHandler(priority = EventPriority.HIGHEST, async = true)
    public void onTest5Event(TestEvent event) throws InterruptedException{
        Thread.sleep(5000);
        log.info("异步测试");
        System.out.println("优先级：高");
    }


    @EventHandler(priority = EventPriority.MONITOR, async = true)
    public void onTest7Event(TestEvent event){

        String eventName = event.getEventName();

        log.info(eventName, "其中 test 值为: " + event.getTest());

    }

}