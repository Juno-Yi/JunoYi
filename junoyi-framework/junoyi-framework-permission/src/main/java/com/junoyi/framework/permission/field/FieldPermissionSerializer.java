package com.junoyi.framework.permission.field;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.FieldPermission;
import com.junoyi.framework.permission.helper.PermissionHelper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 字段权限序列化器
 * <p>
 * 根据用户权限决定字段的序列化方式：
 * - 有读取权限：显示完整值
 * - 无读取权限但有脱敏权限：显示脱敏值
 * - 无任何权限：显示 null
 *
 * @author Fan
 */
public class FieldPermissionSerializer extends JsonSerializer<Object> {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(FieldPermissionSerializer.class);

    private final FieldPermission annotation;

    public FieldPermissionSerializer(FieldPermission annotation) {
        this.annotation = annotation;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // 值为空直接返回
        if (value == null) {
            gen.writeNull();
            return;
        }

        String readPermission = annotation.read();
        
        // 没有配置读取权限，直接返回完整值
        if (StringUtils.isBlank(readPermission)) {
            gen.writeObject(value);
            return;
        }

        // 超级管理员直接返回完整值
        if (PermissionHelper.isSuperAdmin()) {
            log.debug("Super administrator, returns the complete value.");
            gen.writeObject(value);
            return;
        }

        // 有读取权限，返回完整值
        if (PermissionHelper.hasPermission(readPermission)) {
            log.debug("With permission {}, the complete value is returned.", readPermission);
            gen.writeObject(value);
            return;
        }

        // 无读取权限，检查是否需要脱敏
        if (annotation.mask()) {
            // 检查脱敏权限
            String maskPermission = annotation.maskPermission();
            boolean canShowMasked = StringUtils.isBlank(maskPermission) 
                    || PermissionHelper.hasPermission(maskPermission);

            if (canShowMasked && value instanceof String strValue) {
                String maskedValue = MaskUtils.mask(strValue, annotation.maskPattern(), annotation.customMaskRule());
                log.debug("No permission {}. Returning sanitized value: {}.", readPermission, maskedValue);
                gen.writeString(maskedValue);
                return;
            }
        }

        // 无权限，返回 null
        log.warn("No permission {}, returning null.", readPermission);
        gen.writeNull();
    }
}
