package com.junoyi.system.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 角色与权限组关联实体
 *
 * @author Fan
 */
@Data
@TableName("sys_role_group")
public class SysRoleGroup {

    @TableId
    private Long id;

    /**
     * 角色ID，关联系统角色表
     */
    private Long roleId;

    /**
     * 权限组ID，关联权限组表
     */
    private Long groupId;

    /**
     * 关联关系过期时间，超过此时间后关联关系失效
     */
    private Date expireTime;

    /**
     * 记录创建时间
     */
    private Date createTime;
}
