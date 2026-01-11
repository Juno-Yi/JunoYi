package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.domain.dto.CacheKeyQueryDTO;
import com.junoyi.system.domain.vo.CacheKeyVO;
import com.junoyi.system.domain.vo.RedisInfoVO;
import com.junoyi.system.service.ISysCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统缓存监控控制类
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/cache")
@RequiredArgsConstructor
public class SysCacheController extends BaseController {

    private final ISysCacheService sysCacheService;

    /**
     * 获取 redis 信息
     */
    @GetMapping("/info")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.cache.view", "system.api.cache.get"}
    )
    public R<RedisInfoVO> getRedisInfo() {
        return R.ok(sysCacheService.getRedisInfo());
    }

    /**
     * 查询缓存键列表（分页）
     */
    @GetMapping("/keys")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.cache.view", "system.api.cache.get"}
    )
    public R<PageResult<CacheKeyVO>> getCacheKeyList(@RequestBody CacheKeyQueryDTO cacheKeyQuery){

        return R.ok();
    }

}