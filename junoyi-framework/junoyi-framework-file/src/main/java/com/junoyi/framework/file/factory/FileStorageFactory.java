package com.junoyi.framework.file.factory;

import com.junoyi.framework.file.enums.StorageType;
import com.junoyi.framework.file.properties.FileStorageProperties;
import com.junoyi.framework.file.storage.FileStorage;
import com.junoyi.framework.file.storage.LocalFileStorage;
import com.junoyi.framework.file.storage.AliyunOssFileStorage;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 文件存储工厂
 * <p>
 * 根据配置创建对应的文件存储实现
 *
 * @author Fan
 */
@Component
@RequiredArgsConstructor
public class FileStorageFactory {

    private static final JunoYiLog log = JunoYiLogFactory.getLogger(FileStorageFactory.class);

    private final FileStorageProperties properties;

    /**
     * 创建文件存储实例
     *
     * @return 文件存储实例
     */
    public FileStorage createFileStorage() {
        return createFileStorage(properties.getStorageType());
    }

    /**
     * 创建指定类型的文件存储实例
     *
     * @param storageType 存储类型
     * @return 文件存储实例
     */
    public FileStorage createFileStorage(StorageType storageType) {
        log.info("创建文件存储实例: "+storageType.getName());
        
        switch (storageType) {
            case LOCAL:
                return new LocalFileStorage(properties);
                
            case ALIYUN_OSS:
                return new AliyunOssFileStorage(properties);
                
            case MINIO:
                // TODO: 实现MinIO存储
                throw new UnsupportedOperationException("MinIO存储暂未实现");
                
            case QINIU:
                // TODO: 实现七牛云存储
                throw new UnsupportedOperationException("七牛云存储暂未实现");
                
            case TENCENT_COS:
                // TODO: 实现腾讯云COS存储
                throw new UnsupportedOperationException("腾讯云COS存储暂未实现");
                
            default:
                throw new IllegalArgumentException("不支持的存储类型: " + storageType);
        }
    }
}
