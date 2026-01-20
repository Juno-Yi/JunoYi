package com.junoyi.framework.file.strategy;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传策略接口
 *
 * @author Fan
 */
public interface UploadStrategy {

    /**
     * 验证文件
     *
     * @param file 上传的文件
     * @throws IllegalArgumentException 验证失败时抛出异常
     */
    void validate(MultipartFile file);

    /**
     * 获取允许的文件类型（MIME类型）
     */
    List<String> getAllowedContentTypes();

    /**
     * 获取允许的文件扩展名
     */
    List<String> getAllowedExtensions();

    /**
     * 获取最大文件大小（字节）
     */
    long getMaxFileSize();

    /**
     * 获取存储路径前缀
     */
    String getPathPrefix();
}
