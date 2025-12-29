package com.junoyi.system.domain.po;

import lombok.Data;

import java.util.Date;

/**
 * 部门与权限组关联数据实体
 *
 * @author Fan
 */
@Data
public class SysDeptGroup {

    private Long id;

    private Long deptId;

    private Long groupId;

    private Date expireTime;

    private Date createTime;
}