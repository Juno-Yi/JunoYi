package com.junoyi.framework.permission.field;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 字段权限 Jackson 模块
 * <p>
 * 注册字段权限序列化修改器到 Jackson
 *
 * @author Fan
 */
public class FieldPermissionModule extends SimpleModule {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(FieldPermissionModule.class);

    /**
     * 构造函数，初始化字段权限模块
     * <p>
     * 该构造函数会：
     * 1. 调用父类构造函数设置模块名称
     * 2. 设置字段权限序列化修改器
     * 3. 记录模块注册日志
     */
    public FieldPermissionModule() {
        super("FieldPermissionModule");
        // 注册字段权限序列化修改器到当前模块
        setSerializerModifier(new FieldPermissionBeanSerializerModifier());
        log.info("FieldPermissionModule registered");
    }
}
