package com.junoyi.framework.core.exception.auth;


/**
 * 登录异常类，用于处理登录相关的认证异常
 * 继承自AuthException，提供登录特定的异常处理功能
 *
 * @author Fan
 */
public class LoginException extends AuthException {

    /**
     * 构造一个包含错误码、错误信息和域信息的登录异常
     *
     * @param code 错误码
     * @param message 错误信息描述
     * @param domain 域信息
     */
    public LoginException(int code, String message, String domain){
        super(code, message,domain);
    }


    /**
     * 构造一个包含错误码和错误信息的登录异常
     *
     * @param code 错误码
     * @param message 错误信息描述
     */
    public LoginException(int code, String message){
        super(code, message);
    }


    /**
     * 构造一个只包含错误信息的登录异常，默认错误码为401
     *
     * @param message 错误信息描述
     */
    public LoginException(String message){
        super(401,message, null);
    }

    /**
     * 获取域前缀，在父类域前缀基础上追加.LOGIN后缀
     *
     * @return 完整的域前缀字符串
     */
    @Override
    public String getDomainPrefix() {
        return super.getDomainPrefix() + ".LOGIN";
    }
}
