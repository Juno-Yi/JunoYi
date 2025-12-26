package com.junoyi.framework.permission.field;

import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.permission.enums.MaskPattern;

/**
 * 数据脱敏工具类
 *
 * @author Fan
 */
public class MaskUtils {

    private static final String MASK_CHAR = "*";

    /**
     * 根据脱敏模式处理数据
     *
     * @param value       原始值
     * @param maskPattern 脱敏模式
     * @param customRule  自定义规则（格式：startKeep,endKeep,maskChar）
     * @return 脱敏后的值
     */
    public static String mask(String value, MaskPattern maskPattern, String customRule) {
        if (StringUtils.isBlank(value)) {
            return value;
        }

        return switch (maskPattern) {
            case PHONE -> maskPhone(value);
            case ID_CARD -> maskIdCard(value);
            case EMAIL -> maskEmail(value);
            case BANK_CARD -> maskBankCard(value);
            case NAME -> maskName(value);
            case ADDRESS -> maskAddress(value);
            case CUSTOM -> maskCustom(value, customRule);
        };
    }

    /**
     * 手机号脱敏：138****8888
     */
    public static String maskPhone(String phone) {
        if (StringUtils.isBlank(phone) || phone.length() < 7) {
            return maskAll(phone);
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /**
     * 身份证脱敏：110***********1234
     */
    public static String maskIdCard(String idCard) {
        if (StringUtils.isBlank(idCard) || idCard.length() < 8) {
            return maskAll(idCard);
        }
        int keepStart = 3;
        int keepEnd = 4;
        return idCard.substring(0, keepStart) 
                + MASK_CHAR.repeat(idCard.length() - keepStart - keepEnd) 
                + idCard.substring(idCard.length() - keepEnd);
    }

    /**
     * 邮箱脱敏：t***@example.com
     */
    public static String maskEmail(String email) {
        if (StringUtils.isBlank(email) || !email.contains("@")) {
            return maskAll(email);
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) {
            return MASK_CHAR + email.substring(atIndex);
        }
        return email.charAt(0) + MASK_CHAR.repeat(atIndex - 1) + email.substring(atIndex);
    }

    /**
     * 银行卡脱敏：6222 **** **** 1234
     */
    public static String maskBankCard(String bankCard) {
        if (StringUtils.isBlank(bankCard) || bankCard.length() < 8) {
            return maskAll(bankCard);
        }
        String cleaned = bankCard.replaceAll("\\s+", "");
        return cleaned.substring(0, 4) + " **** **** " + cleaned.substring(cleaned.length() - 4);
    }

    /**
     * 姓名脱敏：张* 或 张*明
     */
    public static String maskName(String name) {
        if (StringUtils.isBlank(name)) {
            return name;
        }
        if (name.length() == 1) {
            return MASK_CHAR;
        }
        if (name.length() == 2) {
            return name.charAt(0) + MASK_CHAR;
        }
        return name.charAt(0) + MASK_CHAR.repeat(name.length() - 2) + name.charAt(name.length() - 1);
    }

    /**
     * 地址脱敏：保留前6个字符，后面用***替换
     */
    public static String maskAddress(String address) {
        if (StringUtils.isBlank(address) || address.length() <= 6) {
            return address;
        }
        return address.substring(0, 6) + "***";
    }

    /**
     * 全部隐藏
     */
    public static String maskAll(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        return MASK_CHAR.repeat(Math.min(value.length(), 6));
    }

    /**
     * 自定义脱敏规则
     *
     * @param value      原始值
     * @param customRule 规则格式：startKeep,endKeep,maskChar
     */
    public static String maskCustom(String value, String customRule) {
        if (StringUtils.isBlank(value) || StringUtils.isBlank(customRule)) {
            return maskAll(value);
        }

        String[] parts = customRule.split(",");
        if (parts.length < 2) {
            return maskAll(value);
        }

        try {
            int startKeep = Integer.parseInt(parts[0].trim());
            int endKeep = Integer.parseInt(parts[1].trim());
            String maskChar = parts.length > 2 ? parts[2].trim() : MASK_CHAR;

            if (startKeep + endKeep >= value.length()) {
                return value;
            }

            String start = startKeep > 0 ? value.substring(0, startKeep) : "";
            String end = endKeep > 0 ? value.substring(value.length() - endKeep) : "";
            int maskLength = value.length() - startKeep - endKeep;

            return start + maskChar.repeat(maskLength) + end;
        } catch (NumberFormatException e) {
            return maskAll(value);
        }
    }
}
