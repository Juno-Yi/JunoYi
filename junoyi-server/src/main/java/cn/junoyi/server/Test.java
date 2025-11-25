package cn.junoyi.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Test {
    private final Logger log = LoggerFactory.getLogger(Test.class);
    public Test(){
        log.info("测试普通日志打印输出");
        log.warn("测试警告日志打印输出");
        log.error("测试错误日志打印输出");
    }
}