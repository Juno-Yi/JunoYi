package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.domain.dto.SysRoleDTO;
import com.junoyi.system.domain.dto.SysRoleQueryDTO;
import com.junoyi.system.domain.vo.SysPermGroupVO;
import com.junoyi.system.domain.vo.SysRoleVO;
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
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.role.view", "system.api.role.get"}
    )
    public R<PageResult<SysRoleVO>> getRoleList(SysRoleQueryDTO queryDTO){
        return R.ok(sysRoleService.getRoleList(queryDTO, buildPage()));
    }

    /**
     * 获取角色下拉列表选项
     */
    @GetMapping("/options")
    @Permission(
            value = {"system.ui.role.view", "system.api.role.get"}
    )
    public R<List<SysRoleVO>> getRoleOptions(){
        return R.ok(sysRoleService.getRoleList());
    }

    /**
     * 通过id来获取角色
     */
    @GetMapping("/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.role.view", "system.api.role.get"}
    )
    public R<SysRoleVO> getRoleById(@PathVariable("id") Long id){
        return R.ok(sysRoleService.getRoleById(id));
    }

    /**
     * 添加角色
     */
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.role.view", "system.api.role.add"}
    )
    public R<Void> addRole(@RequestBody SysRoleDTO roleDTO){
        sysRoleService.addRole(roleDTO);
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
    public R<Void> updateRole(@RequestBody SysRoleDTO roleDTO){
        sysRoleService.updateRole(roleDTO);
        return R.ok();
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.role.view", "system.api.role.delete"}
    )
    public R<Void> deleteRole(@PathVariable("id") Long id){
        sysRoleService.deleteRole(id);
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
    public R<Void> deleteRoleBatch(@RequestBody List<Long> ids){
        sysRoleService.deleteRoleBatch(ids);
        return R.ok();
    }

    /**
     * 获取角色已经绑定的权限组
     */
    @GetMapping("/{id}/permission-groups")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.role.view","system.api.role.get"}
    )
    public R<List<SysPermGroupVO>> getRolePermissionGroup(@PathVariable("id") Long id){
        return R.ok(sysRoleService.getRolePermGroups(id));
    }

    /**
     * 更新角色绑定权限组
     */
    @PutMapping("/{id}/permission-groups")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.role.view","system.api.role.update"}
    )
    public R<Void> updateRoleGroup(@PathVariable("id") Long id, @RequestBody List<Long> groupIds){
        sysRoleService.updateRolePermGroups(id, groupIds);
        return R.ok();
    }
}