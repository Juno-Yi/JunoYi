package com.junoyi.framework.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件业务类型枚举
 *
 * @author Fan
 */
@Getter
@AllArgsConstructor
public enum FileBusinessType {

    /**
     * 用户头像
     */
    AVATAR("avatar", "用户头像"),

    /**
     * 文档附件
     */
    DOCUMENT("document", "文档附件"),

    /**
     * 图片文件（商品图片、文章图片等）
     */
    IMAGE("image", "图片文件"),

    /**
     * 视频文件
     */
    VIDEO("video", "视频文件"),

    /**
     * 音频文件
     */
    AUDIO("audio", "音频文件"),

    /**
     * 其他文件
     */
    OTHER("other", "其他文件");

    /**
     * 业务类型代码
     */
    private final String code;

    /**
     * 业务类型名称
     */
    private final String name;

    /**
     * 根据代码获取业务类型
     */
    public static FileBusinessType fromCode(String code) {
        for (FileBusinessType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的文件业务类型: " + code);
    }
}
