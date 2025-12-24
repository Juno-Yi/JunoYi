package com.junoyi.system.controller;

import com.junoyi.framework.captcha.domain.CaptchaResult;
import com.junoyi.framework.core.domain.module.R;
import com.junoyi.system.service.ISysCaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 验证码控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class SysCaptchaController {

    private final ISysCaptchaService captchaService;

    /**
     * 获取图形验证码
     */
    @GetMapping("/image")
    public R<CaptchaResult> getImageCaptcha() {
        CaptchaResult result = captchaService.getImageCaptcha();
        return R.ok(result);
    }

    /**
     * 校验验证码
     */
    @PostMapping("/validate")
    public R<Boolean> validate(@RequestParam("captchaId") String captchaId, @RequestParam("code") String code) {
        boolean valid = captchaService.validate(captchaId, code);
        return R.ok(valid);
    }
}
