package com.junoyi.framework.datasource.enums;

/**
 * 数据源类型枚举
 *
 * @author Fan
 */
public enum DataSourceType {
    /**
     * 主库 - 用于写操作
     */
    MASTER("master", "主数据源"),

    /**
     * 从库 - 用于读操作
     */
    SLAVE("slave", "从数据源"),

    /**
     * 日志库 - 用于日志存储
     */

    /**
     * 报表库 - 用于报表查询
     */
    REPORT("report", "报表数据源");

    /**
     * 数据源名称
     */
    private final String name;

    /**
     * 数据源描述
     */
    private final String description;

    DataSourceType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}