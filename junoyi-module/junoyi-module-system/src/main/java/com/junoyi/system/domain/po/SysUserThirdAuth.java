package com.junoyi.system.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.junoyi.framework.core.domain.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 系统用户第三方登录绑定
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_user_third_auth")
public class SysUserThirdAuth extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 第三方登录类型
     */
    private String authType;

    /**
     * 第三方平台唯一标识符
     */
    private String authKey;

}
