package com.junoyi.system.domain.po;

import com.junoyi.framework.core.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 小程序绑定id
     */
    private String openId;

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
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phonenumber;

    /**
     * 性别
     */
    private String sex;

    /**
     * 密码（加密后）
     */
    private String password;

    /**
     * 哈希加密盐
     */
    private String salt;

    /**
     * 状态
     */
    private int status;

    /**
     * 软删除标识符
     */
    private boolean delFlag;

    /**
     * 密码修改时间
     */
    private Date pwdUpdateTime;


    private SysUserPlatform sysUserPlatform;
}