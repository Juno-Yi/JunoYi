package com.junoyi.framework.stater.banner;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

/**
 * Spring Banner 接口实现类
 *
 * @author Fan
 */
public class JunoYiBanner implements Banner {

    /**
     * 打印自定义Banner
     * @param environment 环境信息
     * @param sourceClass 启动类
     * @param out 输出流
     */
    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        final String BLUE = "\033[38;5;39m";
        final String WHITE = "\033[97m";
        final String RESET = "\033[0m";

        out.println(BLUE +
                " _____                              __    __       \n" +
                "/\\___ \\                            /\\ \\  /\\ \\__    \n" +
                "\\/__/\\ \\  __  __    ___     ___    \\ `\\`\\\\/'/\\_\\   \n" +
                "   _\\ \\ \\/\\ \\/\\ \\ /' _ `\\  / __`\\   `\\ `\\ /'\\/\\ \\  \n" +
                "  /\\ \\_\\ \\ \\ \\_\\ \\/\\ \\/\\ \\/\\ \\L\\ \\    `\\ \\ \\ \\ \\ \\ \n" +
                "  \\ \\____/\\ \\____/\\ \\_\\ \\_\\ \\____/      \\ \\_\\ \\ \\_\\\n" +
                "   \\/___/  \\/___/  \\/_/\\/_/\\/___/        \\/_/  \\/_/\n" +
                "                                                   \n" + RESET
        );

        String junoyiVersion = environment.getProperty("junoyi.version","unknown");
        // 打印JunoYi版本
        out.println(WHITE + "JunoYi Framework Version: "+ junoyiVersion + RESET);
        // 打印SpringBoot版本
        out.println(WHITE + "Spring Boot Version: " + SpringApplication.class.getPackage().getImplementationVersion() + RESET);

        out.println(" ");
        out.println(" ");
    }
}
