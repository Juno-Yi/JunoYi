package com.junoyi.framework.file.strategy.impl;

import com.junoyi.framework.file.strategy.AbstractUploadStrategy;

import java.util.Collections;
import java.util.List;

/**
 * 默认上传策略（其他类型）
 *
 * @author Fan
 */
public class DefaultUploadStrategy extends AbstractUploadStrategy {

    @Override
    public List<String> getAllowedContentTypes() {
        return Collections.emptyList(); // 不限制类型
    }

    @Override
    public List<String> getAllowedExtensions() {
        return Collections.emptyList(); // 不限制扩展名
    }

    @Override
    public long getMaxFileSize() {
        return 20 * 1024 * 1024; // 20MB
    }

    @Override
    public String getPathPrefix() {
        return "other";
    }
}
