package com.junoyi.framework.excel.annotation;

import java.lang.annotation.*;

/**
 * excel 列单元格合并
 * 合并列相同项
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CellMerge {

    /**
     * 列的索引
     */
    int index() default -1;
}
