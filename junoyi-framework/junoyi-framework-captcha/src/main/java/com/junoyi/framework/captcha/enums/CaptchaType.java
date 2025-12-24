package com.junoyi.framework.captcha.enums;

/**
 * 验证码类型
 *
 * @author Fan
 */
public enum CaptchaType {

    /**
     * 图片验证码
     */
    IMAGE,

    /**
     * 滑块验证码
     */
    SLIDER,

    /**
     * 点击验证码
     */
    CLICK,

    /**
     * 行为验证码
     */
    BEHAVIOR,

    /**
     * 邮箱验证码
     */
    EMAIL,

    /**
     * 手机号验证码
     */
    SMS;
}
