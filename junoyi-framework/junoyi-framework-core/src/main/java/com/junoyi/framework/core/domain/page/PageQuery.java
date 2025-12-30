package com.junoyi.framework.core.domain.page;

import lombok.Data;

/**
 * 分页查询参数
 *
 * @author Fan
 */
@Data
public class PageQuery {

    /**
     * 当前页码（从1开始）
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方式（asc/desc）
     */
    private String orderType = "asc";

    /**
     * 获取偏移量（用于 LIMIT offset, size）
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }

    /**
     * 是否升序
     */
    public boolean isAsc() {
        return "asc".equalsIgnoreCase(orderType);
    }
}
