package com.junoyi.framework.file.config;

import com.junoyi.framework.file.properties.FileStorageProperties;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * 文件存储自动配置
 *
 * @author Fan
 */
@AutoConfiguration
@EnableConfigurationProperties(FileStorageProperties.class)
@ComponentScan("com.junoyi.framework.file")
public class FileStorageConfiguration {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(FileStorageConfiguration.class);

    public FileStorageConfiguration(FileStorageProperties properties) {
        log.info("File Manager","File management module initialization completed.");
        log.info("Current storage type：" + properties.getStorageType().getName());
    }
}
