package com.junoyi.framework.captcha.config;

import com.anji.captcha.service.CaptchaService;
import com.anji.captcha.service.impl.CaptchaServiceFactory;
import com.junoyi.framework.captcha.generator.ImageCaptchaGenerator;
import com.junoyi.framework.captcha.generator.SliderCaptchaGenerator;
import com.junoyi.framework.captcha.generator.CaptchaGenerator;
import com.junoyi.framework.captcha.helper.CaptchaHelper;
import com.junoyi.framework.captcha.helper.CaptchaHelperImpl;
import com.junoyi.framework.captcha.properties.CaptchaProperties;
import com.junoyi.framework.captcha.store.CaptchaStore;
import com.junoyi.framework.captcha.store.RedisCaptchaStore;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;

/**
 * 验证码模块自动配置
 *
 * @author Fan
 */
@Configuration
@EnableConfigurationProperties(CaptchaProperties.class)
@ConditionalOnProperty(prefix = "junoyi.captcha", name = "enable", havingValue = "true", matchIfMissing = true)
public class CaptchaConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CaptchaConfiguration.class);

    /**
     * 创建验证码存储器Bean
     * 当容器中不存在CaptchaStore类型的Bean时创建Redis验证码存储器
     *
     * @param redissonClient Redisson客户端实例
     * @return RedisCaptchaStore实例
     */
    @Bean
    @ConditionalOnMissingBean(CaptchaStore.class)
    public CaptchaStore captchaStore(RedissonClient redissonClient) {
        log.info("[Captcha] Redis captcha store initialized");
        return new RedisCaptchaStore(redissonClient);
    }

    /**
     * 创建图片验证码生成器Bean
     *
     * @param properties 验证码配置属性
     * @param captchaStore 验证码存储器
     * @return ImageCaptchaGenerator实例
     */
    @Bean
    public ImageCaptchaGenerator imageCaptchaGenerator(CaptchaProperties properties, CaptchaStore captchaStore) {
        log.info("[Captcha] Image captcha generator initialized, code type: {}", properties.getImage().getCodeType());
        return new ImageCaptchaGenerator(properties, captchaStore);
    }

    /**
     * 创建滑块验证码服务Bean
     * 当容器中不存在CaptchaService类型的Bean时创建AJ-Captcha服务实例
     *
     * @param captchaProperties 验证码配置属性
     * @return CaptchaService实例
     */
    @Bean
    @ConditionalOnMissingBean(CaptchaService.class)
    public CaptchaService captchaService(CaptchaProperties captchaProperties) {
        CaptchaProperties.SliderCaptcha slider = captchaProperties.getSlider();
        log.info("[Captcha] AJ-Captcha service initialized, size: {}x{}, tolerance: {}",
                slider.getWidth(), slider.getHeight(), slider.getTolerance());

        // 配置滑块验证码服务参数
        Properties props = new Properties();
        props.setProperty("captcha.type", "blockPuzzle");
        props.setProperty("captcha.water.mark", slider.getWaterMark());
        props.setProperty("captcha.slip.offset", String.valueOf(slider.getTolerance()));
        props.setProperty("captcha.aes.status", String.valueOf(slider.isAesStatus()));
        props.setProperty("captcha.interference.options", String.valueOf(slider.getInterferenceOptions()));

        return CaptchaServiceFactory.getInstance(props);
    }

    /**
     * 创建滑块验证码生成器Bean
     *
     * @param properties 验证码配置属性
     * @param captchaStore 验证码存储器
     * @param captchaService 验证码服务
     * @return SliderCaptchaGenerator实例
     */
    @Bean
    public SliderCaptchaGenerator sliderCaptchaGenerator(CaptchaProperties properties, CaptchaStore captchaStore, CaptchaService captchaService) {
        log.info("[Captcha] Slider captcha generator initialized");
        return new SliderCaptchaGenerator(properties, captchaStore, captchaService);
    }

    /**
     * 创建验证码助手Bean
     * 当容器中不存在CaptchaHelper类型的Bean时创建验证码助手实现
     *
     * @param properties 验证码配置属性
     * @param generators 验证码生成器列表
     * @return CaptchaHelper实例
     */
    @Bean
    @ConditionalOnMissingBean(CaptchaHelper.class)
    public CaptchaHelper captchaHelper(CaptchaProperties properties, List<CaptchaGenerator> generators) {
        log.info("[Captcha] CaptchaHelper initialized, default type: {}, available generators: {}",
                properties.getType(), generators.size());
        return new CaptchaHelperImpl(properties, generators);
    }
}
