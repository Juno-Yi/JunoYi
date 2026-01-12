package com.junoyi.framework.captcha.helper;

import com.junoyi.framework.captcha.domain.CaptchaResult;
import com.junoyi.framework.captcha.enums.CaptchaType;
import com.junoyi.framework.captcha.generator.CaptchaGenerator;
import com.junoyi.framework.captcha.properties.CaptchaProperties;
import com.junoyi.framework.captcha.exception.CaptchaUnsupportedTypeException;
import com.junoyi.framework.core.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 验证码帮助类实现
 *
 * @author Fan
 */
public class CaptchaHelperImpl implements CaptchaHelper {

    private final CaptchaProperties properties;
    private final Map<CaptchaType, CaptchaGenerator> generatorMap;

    /**
     * 构造函数，初始化验证码帮助类
     *
     * @param properties 验证码配置属性
     * @param generators 验证码生成器列表
     */
    public CaptchaHelperImpl(CaptchaProperties properties, List<CaptchaGenerator> generators) {
        this.properties = properties;
        this.generatorMap = generators.stream()
                .collect(Collectors.toMap(CaptchaGenerator::getType, Function.identity()));
    }

    /**
     * 生成验证码，使用默认配置的验证码类型
     *
     * @return 验证码结果对象
     */
    @Override
    public CaptchaResult generate() {
        return generate(properties.getType());
    }

    /**
     * 根据指定的验证码类型生成验证码
     *
     * @param type 验证码类型
     * @return 验证码结果对象
     */
    @Override
    public CaptchaResult generate(CaptchaType type) {
        CaptchaGenerator generator = getGenerator(type);
        return generator.generate();
    }

    /**
     * 验证验证码是否正确
     *
     * @param captchaId 验证码ID
     * @param code 验证码内容
     * @return 验证结果，正确返回true，否则返回false
     */
    @Override
    public boolean validate(String captchaId, String code) {
        if (StringUtils.isBlank(captchaId) || StringUtils.isBlank(code))
            return false;
        // 默认使用图片验证码生成器验证
        CaptchaGenerator generator = getGenerator(CaptchaType.IMAGE);
        return generator.validate(captchaId, code);
    }


    /**
     * 根据验证码类型获取对应的生成器
     *
     * @param type 验证码类型
     * @return 验证码生成器
     * @throws CaptchaUnsupportedTypeException 当不支持该验证码类型时抛出异常
     */
    private CaptchaGenerator getGenerator(CaptchaType type) {
        CaptchaGenerator generator = generatorMap.get(type);
        if (generator == null) {
            throw new CaptchaUnsupportedTypeException("Unsupported captcha type: " + type);
        }
        return generator;
    }
}
