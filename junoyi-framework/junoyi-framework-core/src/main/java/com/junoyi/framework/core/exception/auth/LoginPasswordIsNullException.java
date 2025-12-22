package com.junoyi.framework.core.exception.auth;

/**
 * 登录密码为空异常类
 * 用于处理用户登录时密码为空的情况
 *
 * @author Fan
 */
public class LoginPasswordIsNullException extends LoginException {

    /**
     * 构造函数
     * @param message 异常信息描述
     */
    public LoginPasswordIsNullException(String message){
        super(401,message,"PASSWORD_IS_NULL");
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
