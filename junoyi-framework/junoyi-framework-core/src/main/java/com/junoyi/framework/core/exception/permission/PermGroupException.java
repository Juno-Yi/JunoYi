package com.junoyi.framework.core.exception.permission;

import com.junoyi.framework.core.domain.base.BaseException;

/**
 * 权限组异常基类
 *
 * @author Fan
 */
public class PermGroupException extends BaseException {

    public PermGroupException(int code, String message, String errorCode) {
        super(code, message, errorCode);
    }

    @Override
    public String getDomainPrefix() {
        return "PERM_GROUP";
    }
}
