package com.junoyi.framework.json.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.junoyi.framework.json.handler.BigNumberSerializer;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * jackson 配置类
 *
 * @author Fan
 */
@AutoConfiguration(before = JacksonAutoConfiguration.class)
public class JacksonConfig {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(JacksonConfig.class);

    /**
     * 创建Jackson2ObjectMapperBuilder的自定义器
     * 用于配置全局的JSON序列化和反序列化规则
     *
     * @return Jackson2ObjectMapperBuilderCustomizer对象，用于自定义ObjectMapper构建器
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer(){
        log.info("Initialize jackson configuration.");
        return builder -> {
            // 配置Java时间模块，处理日期时间类型的序列化和反序列化
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(Long.class, BigNumberSerializer.INSTANCE);
            javaTimeModule.addSerializer(Long.TYPE, BigNumberSerializer.INSTANCE);
            javaTimeModule.addSerializer(BigInteger.class, BigNumberSerializer.INSTANCE);
            javaTimeModule.addSerializer(BigDecimal.class, ToStringSerializer.instance);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
            builder.modules(javaTimeModule);
            builder.timeZone(TimeZone.getDefault());
        };
    }
}
