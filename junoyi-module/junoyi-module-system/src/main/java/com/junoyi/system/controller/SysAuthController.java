package com.junoyi.system.controller;

import com.junoyi.framework.captcha.helper.CaptchaHelper;
import com.junoyi.framework.core.domain.base.BaseController;
import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.exception.captcha.CaptchaExpiredException;
import com.junoyi.framework.core.exception.captcha.CaptchaInvalidException;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.security.context.SecurityContext;
import com.junoyi.framework.security.helper.AuthHelper;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.module.TokenPair;
import com.junoyi.system.convert.LoginConverter;
import com.junoyi.system.convert.SysUserConverter;
import com.junoyi.system.domain.dto.LoginDTO;
import com.junoyi.system.domain.bo.LoginBO;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.vo.AuthVo;
import com.junoyi.system.domain.vo.SysUserVO;
import com.junoyi.system.mapper.SysUserMapper;
import com.junoyi.system.service.ISysAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 系统认证控制器
 * 处理用户登录认证和用户信息获取相关接口
 *
 * @author Fan
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class SysAuthController extends BaseController {

    private final ISysAuthService sysAuthService;
    private final CaptchaHelper captchaHelper;
    private final AuthHelper authHelper;
    private final SysUserMapper sysUserMapper;
    private final LoginConverter loginConverter;
    private final SysUserConverter sysUserConverter;

    /**
     * 用户登录接口
     * 处理用户登录请求，验证用户身份并返回认证结果
     *
     * @param loginDTO 登录请求参数
     * @return R<AuthVo> 统一响应结果，包含 accessToken 和 refreshToken
     */
    @PostMapping("/login")
    public R<AuthVo> login(@Valid @RequestBody LoginDTO loginDTO) {
        // 验证码校验
        if (!StringUtils.isBlank(loginDTO.getCaptchaId())) {
            if (StringUtils.isBlank(loginDTO.getCode()))
                throw new CaptchaInvalidException("验证码不能为空");
            // 验证码验证（validate 返回 false 表示验证失败）
            if (!captchaHelper.validate(loginDTO.getCaptchaId(), loginDTO.getCode()))
                throw new CaptchaExpiredException("验证码错误或已失效");
        }

        // 转换为 LoginBO 并调用登录服务
        LoginBO loginBO = loginConverter.toLoginBO(loginDTO);
        AuthVo authVo = sysAuthService.login(loginBO);

        return R.ok(authVo);
    }

    /**
     * 刷新 accessToken
     * 客户端通过传递 refreshToken，验证 refreshToken 是否有效，
     * 来刷新 accessToken，从而延长登录时间
     *
     * @param refreshToken 刷新令牌
     * @return R<AuthVo> 统一响应结果，包含新的 accessToken
     */
    @PostMapping("/refresh")
    public R<AuthVo> refresh(@RequestParam("refreshToken") String refreshToken) {
        TokenPair tokenPair = authHelper.refresh(refreshToken);
        AuthVo authVo = new AuthVo();
        authVo.setAccessToken(tokenPair.getAccessToken());
        authVo.setRefreshToken(tokenPair.getRefreshToken());
        return R.ok(authVo);
    }

    /**
     * 退出登录
     *
     * @return R<?> 统一响应结果
     */
    @PostMapping("/logout")
    public R<?> logout() {
        authHelper.logout();
        return R.ok("退出成功");
    }

    /**
     * 获取当前登录用户信息
     *
     * @return R<SysUserVO> 统一响应结果，包含用户信息数据
     */
    @GetMapping("/info")
    public R<SysUserVO> getUserInfo() {
        LoginUser loginUser = SecurityContext.get();
        if (loginUser == null) {
            return R.fail("用户未登录");
        }

        // 查询用户详细信息
        SysUser user = sysUserMapper.selectById(loginUser.getUserId());
        if (user == null) {
            return R.fail("用户不存在");
        }

        // 转换为 VO 返回
        SysUserVO userVO = sysUserConverter.toVo(user);
        return R.ok(userVO);
    }
}
