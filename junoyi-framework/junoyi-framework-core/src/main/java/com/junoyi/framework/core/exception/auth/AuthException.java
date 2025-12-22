package com.junoyi.framework.core.exception.auth;

import com.junoyi.framework.core.domain.base.BaseException;

/**
 * 认证异常类，用于处理认证相关的异常情况
 * 继承自BaseException基异常类
 *
 * @author Fan
 */
public class AuthException extends BaseException {

    /**
     * 构造函数1，创建一个认证异常实例
     * @param code 异常码
     * @param message 异常信息描述
     * @param domain 异常当前领域
     */
    public AuthException(int code, String message, String domain){
        super(code, message, domain);
    }

    /**
     * 构造函数，创建一个认证异常实例
     * @param code 异常码
     * @param message 异常信息描述
     */
    public AuthException(int code, String message){
        super(code,message, null);
    }

    /**
     * 构建函数，创建认证异常实例
     * @param message 异常信息描述
     */
    public AuthException(String message){
        super(401,message,null);
    }

    /**
     * 获取域前缀，用于标识异常所属领域
     * @return 返回认证领域的前缀字符串"AUTH"
     */
    @Override
    public String getDomainPrefix() {
        return "AUTH";
    }
}
