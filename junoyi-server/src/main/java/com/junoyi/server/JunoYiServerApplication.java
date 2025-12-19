package com.junoyi.server;


import com.junoyi.framework.stater.JunoYiApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 启动服务端
 *
 * @author Fan
 */
@SpringBootApplication(
    scanBasePackages = {"com.junoyi"},
    exclude = {DataSourceAutoConfiguration.class}
)
@EnableCaching
public class JunoYiServerApplication {
    public static void main(String[] args) {
        JunoYiApplication.run(JunoYiServerApplication.class,args);
        System.out.println("[ JunoYi ] Startup completed. System is now operational.");
    }
}