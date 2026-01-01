package com.junoyi.system.enums;


/**
 * 系统菜单状态枚举类
 *
 * @author Fan
 */
public enum SysMenuStatus {

    ENABLE(1,"启用"),

    DISABLE(0, "禁用");

    private final int code;

    private final String label;

    private SysMenuStatus(int code, String label){
        this.code = code;
        this.label = label;
    }

    public int getCode(){
        return code;
    }

    public String getLabel(){
        return label;
    }

    public static SysMenuStatus fromCode(int code) {
        for (SysMenuStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return ENABLE;
    }

}