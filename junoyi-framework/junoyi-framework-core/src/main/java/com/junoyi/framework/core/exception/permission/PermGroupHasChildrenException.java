package com.junoyi.framework.core.exception.permission;

/**
 * 权限组包含子权限组异常
 *
 * @author Fan
 */
public class PermGroupHasChildrenException extends PermGroupException {

    public PermGroupHasChildrenException(String message) {
        super(501, message, "HAS_CHILDREN");
    }
}
