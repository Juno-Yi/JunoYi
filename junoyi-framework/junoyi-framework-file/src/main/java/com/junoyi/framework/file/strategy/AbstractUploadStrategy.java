package com.junoyi.framework.file.strategy;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传策略抽象类
 *
 * @author Fan
 */
public abstract class AbstractUploadStrategy implements UploadStrategy {

    @Override
    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 验证文件大小
        if (file.getSize() > getMaxFileSize()) {
            throw new IllegalArgumentException(
                    String.format("文件大小超过限制，最大允许 %s", formatFileSize(getMaxFileSize()))
            );
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (StrUtil.isNotBlank(contentType) && !getAllowedContentTypes().isEmpty()) {
            boolean typeMatched = getAllowedContentTypes().stream()
                    .anyMatch(allowed -> contentType.startsWith(allowed));
            if (!typeMatched) {
                throw new IllegalArgumentException(
                        String.format("不支持的文件类型: %s，允许的类型: %s",
                                contentType, String.join(", ", getAllowedContentTypes()))
                );
            }
        }

        // 验证文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isNotBlank(originalFilename) && !getAllowedExtensions().isEmpty()) {
            String extension = FileUtil.extName(originalFilename).toLowerCase();
            if (!getAllowedExtensions().contains(extension)) {
                throw new IllegalArgumentException(
                        String.format("不支持的文件扩展名: %s，允许的扩展名: %s",
                                extension, String.join(", ", getAllowedExtensions()))
                );
            }
        }
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + "B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2fKB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2fMB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2fGB", size / (1024.0 * 1024 * 1024));
        }
    }
}
