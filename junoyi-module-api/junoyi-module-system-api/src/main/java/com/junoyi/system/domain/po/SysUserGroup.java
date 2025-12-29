package com.junoyi.system.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户与权限组关联实体
 *
 * @author Fan
 */
@Data
@TableName("sys_user_group")
public class SysUserGroup {

    @TableId
    private Long id;

    /**
     * 用户ID，关联系统用户表
     */
    private Long userId;

    /**
     * 权限组ID，关联权限组表
     */
    private Long groupId;

    /**
     * 关联关系过期时间，超过此时间关联关系失效
     */
    private Date expireTime;

    /**
     * 记录创建时间
     */
    private Date createTime;
}
