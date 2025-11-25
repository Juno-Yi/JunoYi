package cn.junoyi.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Test {
    private final Logger log = LoggerFactory.getLogger(Test.class);
    public Test(){
        log.info("测试日志系统");
    }
}