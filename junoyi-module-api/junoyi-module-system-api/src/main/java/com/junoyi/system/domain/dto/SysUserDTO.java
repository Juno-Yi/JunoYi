package com.junoyi.system.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户 DTO（接收前端请求参数）
 *
 * @author Fan
 */
@Data
public class SysUserDTO {

    /**
     * 用户ID（更新时使用）
     */
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户账号
     */
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 2, max = 30, message = "用户账号长度必须在2-30之间")
    private String userName;

    /**
     * 用户昵称
     */
    @Size(max = 30, message = "用户昵称长度不能超过30")
    private String nickName;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50")
    private String email;

    /**
     * 手机号
     */
    @Size(max = 11, message = "手机号长度不能超过11")
    private String phonenumber;

    /**
     * 性别（0男 1女 2未知）
     */
    private String sex;

    /**
     * 密码
     */
    private String password;

    /**
     * 状态（0正常 1停用）
     */
    private Integer status;
}
