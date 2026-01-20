package com.junoyi.framework.file.strategy.impl;

import com.junoyi.framework.file.strategy.AbstractUploadStrategy;

import java.util.Arrays;
import java.util.List;

/**
 * 文档上传策略
 *
 * @author Fan
 */
public class DocumentUploadStrategy extends AbstractUploadStrategy {

    @Override
    public List<String> getAllowedContentTypes() {
        return Arrays.asList(
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "text/plain"
        );
    }

    @Override
    public List<String> getAllowedExtensions() {
        return Arrays.asList("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt");
    }

    @Override
    public long getMaxFileSize() {
        return 10 * 1024 * 1024; // 10MB
    }

    @Override
    public String getPathPrefix() {
        return "document";
    }
}
