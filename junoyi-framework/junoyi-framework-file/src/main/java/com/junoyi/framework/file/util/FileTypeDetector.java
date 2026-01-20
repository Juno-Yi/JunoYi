package com.junoyi.framework.file.util;

import cn.hutool.core.util.StrUtil;

/**
 * 文件类型检测工具
 *
 * @author Fan
 */
public class FileTypeDetector {

    /**
     * 根据文件的 ContentType 和扩展名自动检测业务类型
     *
     * @param contentType 文件 MIME 类型
     * @param extension   文件扩展名
     * @return 业务类型路径
     */
    public static String detectBusinessType(String contentType, String extension) {
        // 优先根据 ContentType 判断
        if (StrUtil.isNotBlank(contentType)) {
            contentType = contentType.toLowerCase();
            
            // 图片类型
            if (contentType.startsWith("image/")) {
                return "image";
            }
            
            // 视频类型
            if (contentType.startsWith("video/")) {
                return "video";
            }
            
            // 音频类型
            if (contentType.startsWith("audio/")) {
                return "audio";
            }
            
            // 文档类型
            if (isDocumentType(contentType)) {
                return "document";
            }
        }
        
        // 如果 ContentType 无法判断，根据扩展名判断
        if (StrUtil.isNotBlank(extension)) {
            extension = extension.toLowerCase();
            
            // 图片扩展名
            if (isImageExtension(extension)) {
                return "image";
            }
            
            // 视频扩展名
            if (isVideoExtension(extension)) {
                return "video";
            }
            
            // 音频扩展名
            if (isAudioExtension(extension)) {
                return "audio";
            }
            
            // 文档扩展名
            if (isDocumentExtension(extension)) {
                return "document";
            }
        }
        
        // 无法识别，返回 other
        return "other";
    }

    /**
     * 判断是否为文档类型
     */
    private static boolean isDocumentType(String contentType) {
        return contentType.contains("pdf") ||
               contentType.contains("msword") ||
               contentType.contains("wordprocessingml") ||
               contentType.contains("ms-excel") ||
               contentType.contains("spreadsheetml") ||
               contentType.contains("ms-powerpoint") ||
               contentType.contains("presentationml") ||
               contentType.contains("text/plain");
    }

    /**
     * 判断是否为图片扩展名
     */
    private static boolean isImageExtension(String extension) {
        return "jpg".equals(extension) ||
               "jpeg".equals(extension) ||
               "png".equals(extension) ||
               "gif".equals(extension) ||
               "bmp".equals(extension) ||
               "webp".equals(extension) ||
               "svg".equals(extension) ||
               "ico".equals(extension);
    }

    /**
     * 判断是否为视频扩展名
     */
    private static boolean isVideoExtension(String extension) {
        return "mp4".equals(extension) ||
               "avi".equals(extension) ||
               "mov".equals(extension) ||
               "wmv".equals(extension) ||
               "flv".equals(extension) ||
               "mkv".equals(extension) ||
               "webm".equals(extension) ||
               "mpeg".equals(extension) ||
               "mpg".equals(extension);
    }

    /**
     * 判断是否为音频扩展名
     */
    private static boolean isAudioExtension(String extension) {
        return "mp3".equals(extension) ||
               "wav".equals(extension) ||
               "flac".equals(extension) ||
               "aac".equals(extension) ||
               "ogg".equals(extension) ||
               "wma".equals(extension) ||
               "m4a".equals(extension);
    }

    /**
     * 判断是否为文档扩展名
     */
    private static boolean isDocumentExtension(String extension) {
        return "pdf".equals(extension) ||
               "doc".equals(extension) ||
               "docx".equals(extension) ||
               "xls".equals(extension) ||
               "xlsx".equals(extension) ||
               "ppt".equals(extension) ||
               "pptx".equals(extension) ||
               "txt".equals(extension) ||
               "csv".equals(extension) ||
               "rtf".equals(extension);
    }
}
