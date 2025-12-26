package com.junoyi.system.domain.dto;

import com.junoyi.framework.security.enums.PlatformType;
import lombok.Data;

/**
 * 登录请求传输对象
 * 用于封装用户登录时所需的各种认证信息
 *
 * @author Fan
 */
@Data
public class LoginDTO {

    /**
     * 验证码唯一标识符
     */
    private String captchaId;

    /**
     * 用户名
     * 用户名作为账号进行登录
     */
    private String username;

    /**
     * 邮箱
     * 邮箱作为账号进行登录
     */
    private String email;

    /**
     * 手机号
     * 手机号作为账号进行登录
     */
    private String phonenumber;

    /**
     * 密码
     * 用户登录凭证
     */
    private String password;

    /**
     * 验证码
     * 验证码的值，用于人机验证
     */
    private String code;

    /**
     * 登录平台类型
     * 用于区分不同平台的登录，决定 Token 有效期
     * 可选值：ADMIN_WEB(后台)、FRONT_DESK_WEB(前台)、MINI_PROGRAM(小程序)、APP、DESKTOP_APP(桌面)
     * 如果没有默认就是 ADMIN_WEB后台
     */
    private PlatformType platformType;
}