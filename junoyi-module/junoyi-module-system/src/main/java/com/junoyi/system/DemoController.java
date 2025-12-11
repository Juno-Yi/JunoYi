package com.junoyi.system;

import com.junoyi.framework.event.core.EventBus;
import com.junoyi.system.event.TestEvent;
import com.junoyi.system.listener.TestEventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {


    @GetMapping("/event")
    public void testEvent(){

        // 手动注册事件监听器
        EventBus.get().registerListener(new TestEventListener());


        // 触发事件
        EventBus.get().callEvent(new TestEvent("测试事件"));
    }
}