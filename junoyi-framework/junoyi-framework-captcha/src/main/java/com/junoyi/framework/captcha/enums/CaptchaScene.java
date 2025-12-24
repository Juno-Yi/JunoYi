package com.junoyi.framework.captcha.enums;

/**
 * 验证码使用场景
 *
 * @author Fan
 */
public enum CaptchaScene {

    /**
     * 登录场景
     */
    LOGIN,

    /**
     * 注册场景
     */
    REGISTER,

    /**
     * 重置密码
     */
    RESET_PASSWORD,

    /**
     * 绑定邮箱
     */
    BIND_EMAIL,

    /**
     * 绑定手机号
     */
    BIND_PHONE,

    /**
     * 二次验证
     */
    TWO_FACTOR
}
