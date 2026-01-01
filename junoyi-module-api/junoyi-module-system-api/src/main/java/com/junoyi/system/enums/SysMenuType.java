package com.junoyi.system.enums;

/**
 * 系统菜单类型枚举
 *
 * @author Fan
 */
public enum SysMenuType {

    DIRECTORY(0, "目录"),

    MENU(1, "菜单");

    private final int code;

    private final String label;

    private SysMenuType(int code, String label){
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }
}