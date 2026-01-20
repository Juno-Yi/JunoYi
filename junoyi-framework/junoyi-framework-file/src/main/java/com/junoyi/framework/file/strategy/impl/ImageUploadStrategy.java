package com.junoyi.framework.file.strategy.impl;

import com.junoyi.framework.file.strategy.AbstractUploadStrategy;

import java.util.Arrays;
import java.util.List;

/**
 * 图片上传策略（商品图片、文章图片等）
 *
 * @author Fan
 */
public class ImageUploadStrategy extends AbstractUploadStrategy {

    @Override
    public List<String> getAllowedContentTypes() {
        return Arrays.asList("image/jpeg", "image/png", "image/webp");
    }

    @Override
    public List<String> getAllowedExtensions() {
        return Arrays.asList("jpg", "jpeg", "png", "webp");
    }

    @Override
    public long getMaxFileSize() {
        return 5 * 1024 * 1024; // 5MB
    }

    @Override
    public String getPathPrefix() {
        return "image";
    }
}
