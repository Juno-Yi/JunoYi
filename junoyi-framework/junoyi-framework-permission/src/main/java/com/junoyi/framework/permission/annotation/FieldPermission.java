package com.junoyi.framework.permission.annotation;

import com.junoyi.framework.permission.enums.MaskPattern;

import java.lang.annotation.*;

/**
 * 字段权限注解
 * <p>
 * 用于控制字段级别的读写权限，可标注在实体类字段上
 * <p>
 * 使用示例：
 * <pre>
 * public class User {
 *     private Long id;
 *     private String username;
 *
 *     // 薪资字段需要特定权限才能查看和编辑
 *     &#64;FieldPermission(read = "field.user.salary.read", write = "field.user.salary.write")
 *     private BigDecimal salary;
 *
 *     // 身份证号只读权限
 *     &#64;FieldPermission(read = "field.user.idcard.read")
 *     private String idCard;
 *
 *     // 手机号需要脱敏显示
 *     &#64;FieldPermission(read = "field.user.phone.read", mask = true, maskPattern = "PHONE")
 *     private String phone;
 * }
 * </pre>
 *
 * @author Fan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface FieldPermission {

    /**
     * 读取权限节点
     * <p>
     * 用户需要拥有此权限才能读取该字段，无权限时字段值为 null
     */
    String read() default "";

    /**
     * 写入权限节点
     * <p>
     * 用户需要拥有此权限才能修改该字段，无权限时忽略该字段的更新
     */
    String write() default "";

    /**
     * 是否需要脱敏
     * <p>
     * true: 无完整读取权限时，显示脱敏后的值
     * false: 无权限时直接返回 null
     */
    boolean mask() default false;

    /**
     * 脱敏模式
     * <p>
     * 预定义模式：
     * <ul>
     *   <li>PHONE - 手机号脱敏：138****8888</li>
     *   <li>ID_CARD - 身份证脱敏：110***********1234</li>
     *   <li>EMAIL - 邮箱脱敏：t***@example.com</li>
     *   <li>BANK_CARD - 银行卡脱敏：6222 **** **** 1234</li>
     *   <li>NAME - 姓名脱敏：张*</li>
     *   <li>ADDRESS - 地址脱敏：北京市***</li>
     *   <li>CUSTOM - 自定义脱敏规则</li>
     * </ul>
     */
    MaskPattern maskPattern() default MaskPattern.CUSTOM;

    /**
     * 自定义脱敏规则
     * <p>
     * 当 maskPattern 为 CUSTOM 时生效
     * 格式：startKeep,endKeep,maskChar
     * 示例："3,4,*" 表示保留前3位和后4位，中间用*替换
     */
    String customMaskRule() default "0,0,*";

    /**
     * 脱敏权限节点
     * <p>
     * 拥有此权限时显示脱敏值，否则显示 null
     * 如果为空，则只要有 read 权限就显示脱敏值
     */
    String maskPermission() default "";

    /**
     * 字段分组
     * <p>
     * 用于批量控制一组字段的权限，如 "sensitive"、"financial" 等
     */
    String group() default "";

    /**
     * 字段描述
     * <p>
     * 用于权限管理界面展示
     */
    String description() default "";
}
