package com.junoyi.framework.core.exception.menu;

/**
 * 菜单包含子项异常类
 * 当菜单存在子项时抛出此异常，继承自MenuException
 *
 * @author Fan
 */
public class MenuHasChildrenException extends MenuException {

    /**
     * 构造函数
     * @param message 异常描述信息
     */
    public MenuHasChildrenException(String message){
        super(501,message,"HAS_CHILDREN");
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
