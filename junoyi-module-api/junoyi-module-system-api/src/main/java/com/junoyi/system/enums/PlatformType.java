package com.junoyi.system.enums;


/**
 * 平台类型枚举类
 * 用于定义系统支持的不同平台类型及其对应的编码和标签
 *
 * @author Fan
 */
public enum PlatformType {
    /** 后台网站平台 */
    ADMIN_WEB(0,"后台网站"),

    /** 前台网站平台 */
    FRONT_DESK_WEB(1,"前台网站"),

    /** 小程序平台 */
    MINI_PROGRAM(2,"小程序"),

    /** APP移动应用平台 */
    APP(3,"APP"),

    /** 桌面应用平台 */
    DESKTOP_APP(4,"桌面应用");

    /** 平台类型编码 */
    private final int code;

    /** 平台类型标签描述 */
    private final String label;

    /**
     * 构造函数
     * @param code 平台类型编码
     * @param label 平台类型标签描述
     */
    private PlatformType(int code, String  label){
        this.code = code;
        this.label = label;
    }

    /**
     * 获取平台类型编码
     * @return 平台类型编码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取平台类型标签描述
     * @return 平台类型标签描述
     */
    public String getLabel() {
        return label;
    }
}

