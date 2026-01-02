package com.junoyi.system.domain.vo;

import com.junoyi.framework.permission.annotation.FieldPermission;
import com.junoyi.framework.permission.enums.MaskPattern;
import lombok.Data;

import java.util.Date;

/**
 * 用户 VO（返回给前端）
 *
 * @author Fan
 */
@Data
public class SysUserVO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 邮箱（脱敏）
     */
    @FieldPermission(read = "system.data.user.field.email", mask = true, maskPattern = MaskPattern.EMAIL)
    private String email;

    /**
     * 手机号（脱敏）
     */
    @FieldPermission(read = "system.data.user.field.phone", mask = true, maskPattern = MaskPattern.PHONE)
    private String phonenumber;

    /**
     * 性别
     */
    private String sex;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
