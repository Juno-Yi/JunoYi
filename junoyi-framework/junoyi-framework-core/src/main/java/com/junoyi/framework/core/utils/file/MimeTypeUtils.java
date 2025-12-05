package com.junoyi.framework.core.utils.file;

/**
 * 媒体类型工具类
 *
 * @author Fan
 */
public class MimeTypeUtils {
    public static final String IMAGE_PNG = "image/png";

    public static final String IMAGE_JPG = "image/jpg";

    public static final String IMAGE_JPEG = "image/jpeg";

    public static final String IMAGE_BMP = "image/bmp";

    public static final String IMAGE_GIF = "image/gif";

    public static final String[] IMAGE_EXTENSION = {"bmp", "gif", "jpg", "jpeg", "png"};

    public static final String[] FLASH_EXTENSION = {"swf", "flv"};

    public static final String[] MEDIA_EXTENSION = {"swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi", "mpg",
        "asf", "rm", "rmvb"};

    public static final String[] VIDEO_EXTENSION = {"mp4", "avi", "rmvb"};
    /**
     * 音频扩展名
     */
    public static final String[] AUDIO__EXTENSION = {"mp3", "mp4", "mpeg", "mpga", "m4a", "wav", "webm"};

    public static final String[] DEFAULT_ALLOWED_EXTENSION = {
            // 图片
            "bmp", "gif", "jpg", "jpeg", "png",
            // word excel powerpoint
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt",
            // 压缩文件
            "rar", "zip", "gz", "bz2",
            // 视频格式
            "mp4", "avi", "rmvb",
            // 音频格式
            "mp3", "mp4", "mpeg", "mpga", "m4a", "wav", "webm",
            // pdf
            "pdf"};

    /**
     * 默认大小 50MB
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024;

    /**
     * 根据 MIME 类型获取扩展名
     *
     * @param mimeType MIME类型
     * @return 扩展名
     */
    public static String getExtension(String mimeType) {
        if (mimeType == null) {
            return "";
        }
        switch (mimeType) {
            case IMAGE_PNG:
                return "png";
            case IMAGE_JPG:
            case IMAGE_JPEG:
                return "jpg";
            case IMAGE_GIF:
                return "gif";
            case IMAGE_BMP:
                return "bmp";
            default:
                return "";
        }
    }
}