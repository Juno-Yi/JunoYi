package com.junoyi.framework.core.exception.auth;

/**
 * 登录密码错误异常类
 * 用于处理用户登录时密码输入错误的情况
 *
 * @author Fan
 */
public class LoginPasswordWrongException extends LoginException {

    /**
     * 构造函数
     * @param message 异常信息描述
     */
    public LoginPasswordWrongException(String message){
        super(401,message,"PASSWORD_WRONG");
    }

    /**
     * 获取域前缀
     * @return 域前缀字符串
     */
    @Override
    public String getDomainPrefix() {
        return super.getDomainPrefix();
    }
}
