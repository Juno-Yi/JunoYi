package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.domain.dto.SysMenuDTO;
import com.junoyi.system.domain.dto.SysMenuQueryDTO;
import com.junoyi.system.domain.dto.SysMenuSortDTO;
import com.junoyi.system.domain.vo.SysMenuVO;
import com.junoyi.system.service.ISysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统菜单控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController extends BaseController {

    private final ISysMenuService sysMenuService;

    /**
     * 获取菜单树形列表（不分页，用于构建树形结构）
     */
    @GetMapping("/tree")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"systme.ui.menu.view", "system.api.menu.get"}
    )
    public R<List<SysMenuVO>> getMenuTree(SysMenuQueryDTO queryDTO) {
        return R.ok(sysMenuService.getMenuTree(queryDTO));
    }

    /**
     * 获取菜单列表（分页）
     * 支持参数: pageNum, pageSize
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"systme.ui.menu.view", "system.api.menu.get"}
    )
    public R<PageResult<SysMenuVO>> getMenuList(SysMenuQueryDTO queryDTO) {
        return R.ok(sysMenuService.getMenuPage(queryDTO, buildPage()));
    }

    /**
     * 获取菜单详情
     */
    @GetMapping("/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"systme.ui.menu.view", "system.api.menu.get"}
    )
    public R<SysMenuVO> getMenuById(@PathVariable("id") Long id) {
        return R.ok(sysMenuService.getMenuById(id));
    }

    /**
     * 添加菜单
     */
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"systme.ui.menu.view", "system.api.menu.add"}
    )
    public R<Long> addMenu(@RequestBody SysMenuDTO menuDTO) {
        return R.ok(sysMenuService.addMenu(menuDTO));
    }

    /**
     * 更新菜单
     */
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"systme.ui.menu.view", "system.api.menu.update"}
    )
    public R<Void> updateMenu(@RequestBody SysMenuDTO menuDTO) {
        return sysMenuService.updateMenu(menuDTO) ? R.ok() : R.fail("更新失败");
    }

    /**
     * 更新菜单排序
     */
    @PutMapping("/sort")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.menu.view", "system.api.menu.update"}
    )
    public R<Void> updateMenuSort(@RequestBody SysMenuSortDTO sortDTO){
        return sysMenuService.updateMenuSort(sortDTO.getItems()) ? R.ok() : R.fail("排序更新失败");
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"systme.ui.menu.view", "system.api.menu.delete"}
    )
    public R<?> deleteMenu(@PathVariable("id") Long id) {
        return sysMenuService.deleteMenu(id) ? R.ok() : R.fail("删除失败");
    }

}
