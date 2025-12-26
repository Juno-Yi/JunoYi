package com.junoyi.framework.core.exception.captcha;

/**
 * 验证码无效异常类
 * 继承自CaptchaException，用于处理验证码无效的情况
 *
 * @author Fan
 */
public class CaptchaInvalidException extends CaptchaException {

    /**
     * 构造函数
     * @param message 异常信息描述
     */
    public CaptchaInvalidException(String message){
        super(501, message, "INVALID");
    }


    /**
     * 获取域名前缀
     * @return 返回父类的域名前缀
     */
    @Override
    public String getDomainPrefix() {
        return super.getDomainPrefix();
    }
}
