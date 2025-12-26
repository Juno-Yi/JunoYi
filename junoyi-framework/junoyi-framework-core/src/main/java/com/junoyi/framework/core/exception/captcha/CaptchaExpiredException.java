package com.junoyi.framework.core.exception.captcha;

/**
 * 验证码过期异常类
 * 继承自CaptchaException，用于处理验证码已过期的异常情况
 *
 * @author Fan
 */
public class CaptchaExpiredException extends CaptchaException{

    /**
     * 构造函数
     * @param message 异常描述信息
     */
    public CaptchaExpiredException(String message){
        super(501, message, "EXPIRED");
    }

    /**
     * 获取领域前缀
     * @return 返回父类的领域前缀
     */
    @Override
    public String getDomainPrefix() {
        return super.getDomainPrefix();
    }
}
