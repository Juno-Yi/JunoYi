package com.junoyi.demo.domain;

import com.junoyi.framework.permission.annotation.FieldPermission;
import com.junoyi.framework.permission.enums.MaskPattern;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户信息 VO（字段权限测试）
 *
 * @author Fan
 */
@Data
public class UserInfoVO {

    private Long id;

    private String username;

    private String nickName;

    /**
     * 手机号 - 需要权限才能查看完整，否则脱敏显示
     */
    @FieldPermission(read = "field.user.phone", mask = true, maskPattern = MaskPattern.PHONE)
    private String phone;

    /**
     * 身份证号 - 需要权限才能查看完整，否则脱敏显示
     */
    @FieldPermission(read = "field.user.idcard", mask = true, maskPattern = MaskPattern.ID_CARD)
    private String idCard;

    /**
     * 邮箱 - 需要权限才能查看完整，否则脱敏显示
     */
    @FieldPermission(read = "field.user.email", mask = true, maskPattern = MaskPattern.EMAIL)
    private String email;

    /**
     * 薪资 - 需要权限才能查看，否则返回 null
     */
    @FieldPermission(read = "field.user.salary")
    private BigDecimal salary;

    /**
     * 银行卡号 - 需要权限才能查看完整，否则脱敏显示
     */
    @FieldPermission(read = "field.user.bankcard", mask = true, maskPattern = MaskPattern.BANK_CARD)
    private String bankCard;

    /**
     * 家庭住址 - 需要权限才能查看完整，否则脱敏显示
     */
    @FieldPermission(read = "field.user.address", mask = true, maskPattern = MaskPattern.ADDRESS)
    private String address;
}
