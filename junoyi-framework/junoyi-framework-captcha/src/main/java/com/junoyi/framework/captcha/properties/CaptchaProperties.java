package com.junoyi.framework.captcha.properties;

import com.junoyi.framework.captcha.enums.CaptchaType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 验证码配置属性
 *
 * @author Fan
 */
@Data
@ConfigurationProperties(prefix = "junoyi.captcha")
public class CaptchaProperties {
    /**
     * 是否启用验证码
     */
    private boolean enable = true;
    /**
     * 默认验证码类型
     */
    private CaptchaType type = CaptchaType.SLIDER;
    /**
     * 验证码过期时间 (秒)
     */
    private int expireSeconds = 120;
    /**
     * 图片验证码配置
     */
    private ImageCaptcha image = new ImageCaptcha();
    /**
     * 滑块验证码配置
     */
    private SliderCaptcha slider = new SliderCaptcha();
    /**
     * 点选验证码配置
     */
    private ClickCaptcha click = new ClickCaptcha();

    @Data
    public static class ImageCaptcha {
        /**
         * 验证码长度
         */
        private int length = 4;
        /**
         * 图片宽度
         */
        private int width = 150;
        /**
         * 图片高度
         */
        private int height = 50;
        /**
         * 干扰圆圈数量
         */
        private int circleCount = 20;
        /**
         * 验证码类型: math(数学运算), char(字符)
         */
        private String codeType = "char";
    }

    @Data
    public static class SliderCaptcha {
        /**
         * 滑块容差值 (像素)
         */
        private int tolerance = 5;
        /**
         * 背景图片宽度
         */
        private int width = 310;
        /**
         * 背景图片高度
         */
        private int height = 155;
    }

    @Data
    public static class ClickCaptcha {
        /**
         * 需要点击的文字数量
         */
        private int wordCount = 4;
        /**
         * 点击容差值 (像素)
         */
        private int tolerance = 10;
    }
}
