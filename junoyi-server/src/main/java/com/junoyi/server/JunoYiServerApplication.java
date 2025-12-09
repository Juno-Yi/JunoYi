package com.junoyi.server;


import com.junoyi.framework.stater.JunoYiApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动服务端
 *
 * @author Fan
 */
@SpringBootApplication(
    scanBasePackages = {"com.junoyi"},
    exclude = {DataSourceAutoConfiguration.class}
)
public class JunoYiServerApplication {
    public static void main(String[] args) {
        JunoYiApplication.run(JunoYiServerApplication.class,args);
        System.out.println("(♥◠‿◠)ﾉﾞ  JunoYi启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}