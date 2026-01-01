package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.domain.dto.SysRoleQueryDTO;
import com.junoyi.system.domain.vo.SysRoleVo;
import com.junoyi.system.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统角色管理控制类
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController extends BaseController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysUserController.class);

    private final ISysRoleService sysRoleService;

    /**
     * 获取角色列表（分页）
     */
    @GetMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.role.view", "system.api.role.get"}
    )
    public R<PageResult<SysRoleVo>> getRoleList(SysRoleQueryDTO queryDTO){
        return R.ok(sysRoleService.getRoleList(queryDTO, buildPage()));
    }

    /**
     * 获取角色下拉列表选项
     */
    @GetMapping("/options")
    @Permission(
            value = {"system.ui.role.view", "system.api.role.get"}
    )
    public R<List<SysRoleVo>> getRoleOptions(){
        return R.ok();
    }

    /**
     * 通过id来获取角色
     */
    @GetMapping("/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.role.view", "system.api.role.get"}
    )
    public R<SysRoleVo> getRoleById(@PathVariable("id") Long id){
        return R.ok();
    }

    /**
     * 添加角色
     */
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.role.view", "system.api.role.add"}
    )
    public R<Void> addRole(){
        return R.ok();
    }

    /**
     * 修改角色
     */
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.role.view", "system.api.role.update"}
    )
    public R<Void> updateRole(){
        return R.ok();
    }

    /**
     * 删除角色
     */
    @DeleteMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.role.view", "system.api.role.delete"}
    )
    public R<Void> deleteRole(){
        return R.ok();
    }

    /**
     * 批量删除角色
     */
    @DeleteMapping("/batch")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.role.view", "system.api.role.delete"}
    )
    public R<Void> deleteRoleBatch(){
        return R.ok();
    }
}