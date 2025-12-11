package com.junoyi.system.event;

import com.junoyi.framework.event.event.Event;
import lombok.Data;

/**
 * 自定义Test事件
 *
 * @author Fan
 */
@Data
public class TestEvent implements Event {

    private String test;

    public TestEvent(String test){
        this.test = test;
    }
}