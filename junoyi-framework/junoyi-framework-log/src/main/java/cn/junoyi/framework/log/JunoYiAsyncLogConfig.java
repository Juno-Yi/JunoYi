package cn.junoyi.framework.log;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 异步日志配置类
 * 配置异步日志线程池
 *
 * @author Fan
 */
@Configuration
@EnableAsync
@ConditionalOnProperty(prefix = "junoyi.log.async", name = "enabled", havingValue = "true")
public class JunoYiAsyncLogConfig {

    private final JunoYiLogProperties logProperties;

    public JunoYiAsyncLogConfig(JunoYiLogProperties logProperties) {
        this.logProperties = logProperties;
    }

    /**
     * 异步日志线程池
     */
    @Bean(name = "junoYiLogExecutor")
    public Executor junoYiLogExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 设置核心线程数
        executor.setCorePoolSize(logProperties.getAsync().getThreadPoolSize());
        
        // 设置最大线程数
        executor.setMaxPoolSize(logProperties.getAsync().getThreadPoolSize() * 2);
        
        // 设置队列容量
        executor.setQueueCapacity(logProperties.getAsync().getQueueSize());
        
        // 设置线程名前缀
        executor.setThreadNamePrefix("JunoYi-Log-");
        
        // 设置拒绝策略
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 设置等待时间
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        return executor;
    }
}
