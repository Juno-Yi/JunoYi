package com.junoyi.demo.listener;

import com.junoyi.framework.event.annotation.EventHandler;
import com.junoyi.framework.event.core.Listener;
import com.junoyi.framework.event.domain.spring.SpringApplicationStartingEvent;

public class Test2EventListener implements Listener {

    @EventHandler
    public void onTestEven32t(SpringApplicationStartingEvent event){
        System.out.println("===============");
        System.out.println(" 测试  SpringApplicationStartingEvent 事件监听");
        System.out.println("===============");
    }


}