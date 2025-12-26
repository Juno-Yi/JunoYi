package com.junoyi.framework.permission.enums;

/**
 * 脱敏枚举类
 *
 * @author Fan
 */
public enum MaskPattern {
    /**
     * 手机号脱敏类型
     */
    PHONE,

    /**
     * 身份证号脱敏类型
     */
    ID_CARD,

    /**
     * 邮箱脱敏类型
     */
    EMAIL,

    /**
     * 银行卡号脱敏类型
     */
    BANK_CARD,

    /**
     * 姓名脱敏类型
     */
    NAME,

    /**
     * 地址脱敏类型
     */
    ADDRESS,

    /**
     * 自定义脱敏类型
     */
    CUSTOM
}
