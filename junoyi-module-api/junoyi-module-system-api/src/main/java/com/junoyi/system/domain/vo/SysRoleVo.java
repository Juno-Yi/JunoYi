package com.junoyi.system.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 系统角色响应数据
 *
 * @author Fan
 */
@Data
public class SysRoleVo {

    /**
     * 角色 ID
     */
    private Long id;

    /**
     * 角色名字
     */
    private String roleName;

    /**
     * 角色标识
     */
    private String roleKey;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 数据范围
     */
    private String dataScope;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 删除标识符（软删除）
     */
    private Boolean delFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;
}