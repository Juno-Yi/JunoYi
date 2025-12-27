package com.junoyi.framework.core.domain.base;


import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.utils.SecurityUtils;

/**
 * BaseController类是所有控制器类的基类
 * 提供基础的日志记录功能，子类控制器可以直接使用log对象进行日志输出
 *
 * @author Fan
 */
public class BaseController {
    /**
     * 日志记录器实例
     * 通过JunoYiLogFactory工厂创建，用于记录控制器层的日志信息
     */
    protected final JunoYiLog log = JunoYiLogFactory.getLogger(this.getClass());


    /**
     * 获取当前登录的用户
     * @return 返回 LoginUser 对象
     */
    protected LoginUser getLoginUser(){
        return SecurityUtils.getLoginUser();
    }

    /**
     * 获取当前登录用户的用户名
     * @return 返回用户名字符串
     */
    protected String getUserName(){
        return SecurityUtils.getUserName();
    }

    /**
     * 获取当前登录用户的昵称
     * @return 返回昵称字符串
     */
    protected String getNickName(){
        return SecurityUtils.getNickName();
    }

    /**
     * 获取当前登录用户的ID
     * @return 返回用户ID的Long类型值
     */
    protected Long getUserId(){
        return SecurityUtils.getUserId();
    }

}
