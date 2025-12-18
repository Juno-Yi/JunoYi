package com.junoyi.system.domain.po;

import lombok.Data;

import java.util.Date;

/**
 * 系统用户平台信息实体类
 * 用于存储用户在不同平台上的登录信息和身份标识
 *
 * @author Fan
 */
@Data
public class SysUserPlatform {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 平台类型
     */
    private int platformType;

    /**
     * 平台唯一标识符
     */
    private String platformUID;

    /**
     * 登录IP地址
     */
    private String loginIp;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 用户令牌
     */
    private String token;
}
