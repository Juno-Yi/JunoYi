package com.junoyi.framework.core.exception.menu;

import com.junoyi.framework.core.domain.base.BaseException;

/**
 * 菜单异常类，继承自BaseException
 * 用于处理菜单相关的异常情况
 *
 * @author Fan
 */
public class MenuException extends BaseException {

    /**
     * 构造函数，创建带有错误码、消息和域的菜单异常
     * @param code 错误码
     * @param message 异常消息
     * @param domain 异常域
     */
    public MenuException(int code, String message, String domain){
        super(code, message, domain);
    }

    /**
     * 构造函数，创建带有错误码和消息的菜单异常
     * @param code 错误码
     * @param message 异常消息
     */
    public MenuException(int code, String message) {
        super(code, message, null);
    }

    /**
     * 构造函数，创建带有默认错误码的消息的菜单异常
     * @param message 异常消息
     */
    public MenuException(String message){
        super(501, message, null);
    }

    /**
     * 获取域前缀
     * @return 返回"MENU"作为域前缀
     */
    @Override
    public String getDomainPrefix() {
        return "MENU";
    }
}
