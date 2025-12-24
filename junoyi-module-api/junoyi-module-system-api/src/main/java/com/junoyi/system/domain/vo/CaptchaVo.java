package com.junoyi.system.domain.vo;


import com.junoyi.framework.captcha.enums.CaptchaType;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 验证码值对象类
 * 用于封装验证码相关的数据信息，包括验证码ID、类型和具体数据
 *
 * @author Fan
 */
@Data
@Builder
@RequiredArgsConstructor
public class CaptchaVo {
    /**
     * 验证码唯一标识符
     */
    private String captchaId;

    /**
     * 验证码类型
     */
    private CaptchaType type;

    /**
     * 验证码具体数据，根据类型不同可能包含不同的数据结构
     */
    private Object data;
}
