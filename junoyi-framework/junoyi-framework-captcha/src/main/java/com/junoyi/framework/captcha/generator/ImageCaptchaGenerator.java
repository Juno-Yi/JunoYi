package com.junoyi.framework.captcha.generator;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.util.IdUtil;
import com.junoyi.framework.captcha.domain.CaptchaResult;
import com.junoyi.framework.captcha.enums.CaptchaType;
import com.junoyi.framework.captcha.properties.CaptchaProperties;
import com.junoyi.framework.captcha.store.CaptchaStore;

import java.awt.*;

/**
 * 图片验证码生成器
 *
 * @author Fan
 */
public class ImageCaptchaGenerator implements CaptchaGenerator {

    private final CaptchaProperties properties;
    private final CaptchaStore captchaStore;

    public ImageCaptchaGenerator(CaptchaProperties properties, CaptchaStore captchaStore) {
        this.properties = properties;
        this.captchaStore = captchaStore;
    }

    @Override
    public CaptchaType getType() {
        return CaptchaType.IMAGE;
    }

    @Override
    public CaptchaResult generate() {
        CaptchaProperties.ImageCaptcha config = properties.getImage();
        String captchaId = IdUtil.fastSimpleUUID();
        String code;
        String imageBase64;

        // 使用圆圈干扰验证码，视觉效果更好
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(
                config.getWidth(), config.getHeight(), config.getLength(), config.getCircleCount());

        // 设置字体：粗体，适中大小
        captcha.setFont(new Font("Arial", Font.BOLD, 32));
        // 浅灰色背景
        captcha.setBackground(new Color(245, 245, 245));

        if ("math".equalsIgnoreCase(config.getCodeType())) {
            // 数学运算验证码
            MathGenerator mathGenerator = new MathGenerator(1);
            captcha.setGenerator(mathGenerator);
            captcha.createCode();
            code = calculateMathExpression(captcha.getCode());
        } else {
            // 字符验证码 - 排除易混淆字符
            RandomGenerator randomGenerator = new RandomGenerator("2345678abcdefghjkmnpqrstuvwxyz", config.getLength());
            captcha.setGenerator(randomGenerator);
            captcha.createCode();
            code = captcha.getCode();
        }

        imageBase64 = captcha.getImageBase64Data();

        // 存储验证码（忽略大小写）
        captchaStore.save(captchaId, code.toLowerCase(), properties.getExpireSeconds());

        return new CaptchaResult()
                .setCaptchaId(captchaId)
                .setType(CaptchaType.IMAGE)
                .setImage(imageBase64)
                .setExpireSeconds(properties.getExpireSeconds());
    }

    @Override
    public boolean validate(String captchaId, Object params) {
        if (params == null) return false;
        String inputCode = params.toString().toLowerCase();
        return captchaStore.validateAndRemove(captchaId, inputCode);
    }

    /**
     * 计算数学表达式
     */
    private String calculateMathExpression(String expression) {
        try {
            String exp = expression.replace("=", "").trim();
            if (exp.contains("+")) {
                String[] parts = exp.split("\\+");
                return String.valueOf(Integer.parseInt(parts[0].trim()) + Integer.parseInt(parts[1].trim()));
            } else if (exp.contains("-")) {
                String[] parts = exp.split("-");
                return String.valueOf(Integer.parseInt(parts[0].trim()) - Integer.parseInt(parts[1].trim()));
            } else if (exp.contains("*") || exp.contains("×")) {
                String[] parts = exp.split("[*×]");
                return String.valueOf(Integer.parseInt(parts[0].trim()) * Integer.parseInt(parts[1].trim()));
            }
        } catch (Exception ignored) {
        }
        return "0";
    }
}
