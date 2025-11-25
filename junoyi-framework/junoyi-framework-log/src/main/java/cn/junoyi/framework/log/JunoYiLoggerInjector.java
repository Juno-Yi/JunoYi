package cn.junoyi.framework.log;

import cn.junoyi.framework.log.annotation.JunoYILogger;
import cn.junoyi.framework.log.core.JunoLog;
import cn.junoyi.framework.log.core.JunoLogFactory;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Modifier;

/**
 * JunoYi框架Logger注解注入器
 * 使用ByteBuddy在Spring Bean创建时自动注入log字段
 * 
 * @author Fan
 */
@Component
public class JunoYiLoggerInjector implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        
        // 检查类是否有@JunoYILogger注解
        if (beanClass.isAnnotationPresent(JunoYILogger.class)) {
            System.out.println("🔍 检测到@JunoYILogger注解在类: " + beanClass.getName());
            return enhanceBeanWithLogger(bean, beanClass);
        }
        
        return bean;
    }

    /**
     * 使用ByteBuddy增强Bean，添加log字段
     */
    private Object enhanceBeanWithLogger(Object originalBean, Class<?> originalClass) {
        try {
            // 检查是否已经有log字段
            try {
                originalClass.getDeclaredField("log");
                System.out.println("类已存在log字段: " + originalClass.getName());
                // 如果已经有log字段，直接返回
                return originalBean;
            } catch (NoSuchFieldException e) {
                // 没有log字段，需要添加
                System.out.println("开始为类添加log字段: " + originalClass.getName());
            }

            // 使用ByteBuddy创建增强类
            DynamicType.Builder<?> builder = new ByteBuddy()
                    .subclass(originalClass)
                    .name(originalClass.getName() + "$JunoYiLoggerEnhanced")
                    .modifiers(Modifier.PUBLIC);

            // 添加log字段
            builder = builder.defineField("log", JunoLog.class, Modifier.PRIVATE | Modifier.FINAL)
                    .defineConstructor(Modifier.PUBLIC)
                    .intercept(MethodCall.invoke(originalClass.getConstructor())
                            .andThen(FieldAccessor.ofField("log").setsValue(
                                    JunoLogFactory.getLogger(originalClass)
                            )));

            // 创建增强类
            Class<?> enhancedClass = builder.make()
                    .load(originalClass.getClassLoader())
                    .getLoaded();

            // 创建增强实例
            Object enhancedBean = enhancedClass.getDeclaredConstructor().newInstance();

            // 复制原始Bean的属性到增强Bean
            copyProperties(originalBean, enhancedBean);

            System.out.println("🎉 成功创建增强Bean: " + enhancedClass.getName());
            return enhancedBean;

        } catch (Exception e) {
            // 如果增强失败，返回原始Bean并记录警告
            System.err.println("JunoYiLogger注入失败: " + e.getMessage());
            e.printStackTrace();
            return originalBean;
        }
    }

    /**
     * 复制原始Bean的属性到增强Bean
     */
    private void copyProperties(Object source, Object target) throws Exception {
        // 简单的属性复制，可以根据需要扩展
        // 这里使用反射复制所有非静态字段
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        java.lang.reflect.Field[] fields = sourceClass.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers()) && 
                !Modifier.isFinal(field.getModifiers()) &&
                !field.getName().equals("log")) {
                
                field.setAccessible(true);
                Object value = field.get(source);
                
                try {
                    java.lang.reflect.Field targetField = targetClass.getDeclaredField(field.getName());
                    targetField.setAccessible(true);
                    targetField.set(target, value);
                } catch (NoSuchFieldException e) {
                    // 目标类可能没有这个字段，跳过
                }
            }
        }
    }
}
