package com.junoyi.system.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 部门与权限组关联实体
 *
 * @author Fan
 */
@Data
@TableName("sys_dept_group")
public class SysDeptGroup {

    @TableId
    private Long id;

    private Long deptId;

    private Long groupId;

    private Date expireTime;

    private Date createTime;
}