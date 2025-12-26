package com.junoyi.framework.permission.field;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.FieldPermission;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Bean 序列化修改器
 * <p>
 * 扫描带有 @FieldPermission 注解的字段，替换其序列化器
 *
 * @author Fan
 */
public class FieldPermissionBeanSerializerModifier extends BeanSerializerModifier {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(FieldPermissionBeanSerializerModifier.class);

    /**
     * 修改Bean的序列化属性
     * 遍历所有属性，查找带有FieldPermission注解的字段并替换为其自定义序列化器
     *
     * @param config 序列化配置对象
     * @param beanDesc Bean描述对象，包含Bean的元数据信息
     * @param beanProperties Bean属性写入器列表
     * @return 修改后的Bean属性写入器列表
     */
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        // 遍历所有Bean属性写入器，查找带有FieldPermission注解的字段
        for (BeanPropertyWriter writer : beanProperties) {
            FieldPermission annotation = writer.getAnnotation(FieldPermission.class);
            if (annotation != null) {
                log.info("Registering FieldPermissionSerializer for field {}.{}", beanDesc.getBeanClass().getSimpleName(), writer.getName());
                writer.assignSerializer(new FieldPermissionSerializer(annotation));
            }
        }
        return beanProperties;
    }
}
