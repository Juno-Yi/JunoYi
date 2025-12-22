package com.junoyi.framework.core.domain.base;

/**
 * 基础异常类
 * <p>
 * 所有业务异常都应该继承此类，作为系统异常处理体系的基础类。
 * 该类继承自RuntimeException，属于非受检异常，无需显式声明或捕获。
 * </p>
 *
 * @author Fan
 */
public abstract class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * http 状态码
     */
    private final int code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 异常当前领域
     */
    private final String domain;

    /**
     * 构造一个基础异常实例
     *
     * @param code    HTTP状态码
     * @param message 错误消息
     * @param domain  异常所属领域
     */
    public BaseException(int code, String message, String domain) {
        super(message);
        this.code = code;
        this.message = message;
        this.domain = domain;
    }

    /**
     * 获取HTTP状态码
     *
     * @return HTTP状态码
     */
    public int getCode(){
        return code;
    }


    /**
     * 获取错误消息
     *
     * @return 错误消息
     */
    @Override
    public String getMessage() {
        return message;
    }


    /**
     * 获取异常当前领域
     *
     * @return 异常领域
     */
    public String getDomain(){
        return domain;
    }

    /**
     * 获取领域前缀
     *
     * @return 领域前缀
     */
    public abstract String getDomainPrefix();

    /**
     * 获取完整的领域标识，由领域前缀和领域名称组成
     *
     * @return 完整的领域标识
     */
    public String getFullDomain(){
        return getDomainPrefix() + "." + domain;
    }
}

