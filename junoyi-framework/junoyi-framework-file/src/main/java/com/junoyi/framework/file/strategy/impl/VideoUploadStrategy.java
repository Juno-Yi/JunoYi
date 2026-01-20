package com.junoyi.framework.file.strategy.impl;

import com.junoyi.framework.file.strategy.AbstractUploadStrategy;

import java.util.Arrays;
import java.util.List;

/**
 * 视频上传策略
 *
 * @author Fan
 */
public class VideoUploadStrategy extends AbstractUploadStrategy {

    @Override
    public List<String> getAllowedContentTypes() {
        return Arrays.asList("video/mp4", "video/mpeg", "video/quicktime", "video/x-msvideo");
    }

    @Override
    public List<String> getAllowedExtensions() {
        return Arrays.asList("mp4", "mpeg", "mpg", "mov", "avi");
    }

    @Override
    public long getMaxFileSize() {
        return 100 * 1024 * 1024; // 100MB
    }

    @Override
    public String getPathPrefix() {
        return "video";
    }
}
