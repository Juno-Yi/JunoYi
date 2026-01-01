package com.junoyi.system.domain.dto;

import lombok.Data;

/**
 * 系统角色查询传输数据
 *
 * @author Fan
 */
@Data
public class SysRoleQueryDTO {

    /**
     * 角色名字
     */
    private String roleName;

    /**
     * 角色标识
     */
    private String roleKey;

    /**
     * 状态
     */
    private Integer status;

}