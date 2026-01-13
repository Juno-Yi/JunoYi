package com.junoyi.framework.datasource.datascope.annotation;

import java.lang.annotation.*;

/**
 * 忽略数据范围注解
 * <p>
 * 标注在 Mapper 类或方法上，表示该查询不需要数据范围过滤。
 * 适用于：
 * - 系统内部查询（如登录时查询用户信息）
 * - 统计报表查询
 * - 需要查看全部数据的场景
 *
 * @author Fan
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreDataScope {
}
