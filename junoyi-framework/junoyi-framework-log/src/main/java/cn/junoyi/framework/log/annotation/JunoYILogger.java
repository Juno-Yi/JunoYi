package cn.junoyi.framework.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JunoYi框架日志注解
 * 在类上使用，可以在在此类中全局调用log对象去调用info()、warn()、error()等日志方法
 *
 * @author Fan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JunoYILogger {
}
