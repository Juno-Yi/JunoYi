package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.domain.dto.CacheKeyQueryDTO;
import com.junoyi.system.domain.vo.CacheKeyDetailVO;
import com.junoyi.system.domain.vo.CacheKeyVO;
import com.junoyi.system.domain.vo.RedisInfoVO;
import com.junoyi.system.service.ISysCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            value = {"system.ui.cache.view", "system.api.cache.get.info"}
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
            value = {"system.ui.cache.view", "system.api.cache.get.keys"}
    )
    public R<PageResult<CacheKeyVO>> getCacheKeyList(CacheKeyQueryDTO query) {
        return R.ok(sysCacheService.getCacheKeyList(query, getPageQuery()));
    }

    /**
     * 查询缓存详情
     */
    @GetMapping("/key")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.cache.view", "system.api.cache.get.key"}
    )
    public R<CacheKeyDetailVO> getCacheKeyDetail(@RequestParam("key") String key) {
        return R.ok(sysCacheService.getCacheKeyDetail(key));
    }

    /**
     * 删除指定缓存
     */
    @DeleteMapping("/key")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.cache.view", "system.api.cache.delete.key"}
    )
    public R<Void> deleteCache(@RequestParam("key") String key) {
        sysCacheService.deleteCacheKey(key);
        return R.ok();
    }

    /**
     * 批量删除缓存
     */
    @DeleteMapping("/batch")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.cache.view", "system.api.cache.delete.batch"}
    )
    public R<Void> deleteCacheBatch(@RequestBody List<String> keys) {
        sysCacheService.deleteCacheBatch(keys);
        return R.ok();
    }

    /**
     * 清理所有缓存
     */
    @DeleteMapping("/clear")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.cache.view", "system.api.cache.clear"}
    )
    public R<Void> clearAllCache() {
        sysCacheService.clearAllCache();
        return R.ok();
    }

}