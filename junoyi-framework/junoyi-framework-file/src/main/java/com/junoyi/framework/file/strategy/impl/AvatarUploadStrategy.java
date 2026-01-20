package com.junoyi.framework.file.strategy.impl;

import com.junoyi.framework.file.strategy.AbstractUploadStrategy;

import java.util.Arrays;
import java.util.List;

/**
 * 头像上传策略
 *
 * @author Fan
 */
public class AvatarUploadStrategy extends AbstractUploadStrategy {

    @Override
    public List<String> getAllowedContentTypes() {
        return Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp");
    }

    @Override
    public List<String> getAllowedExtensions() {
        return Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    }

    @Override
    public long getMaxFileSize() {
        return 2 * 1024 * 1024; // 2MB
    }

    @Override
    public String getPathPrefix() {
        return "avatar";
    }
}
