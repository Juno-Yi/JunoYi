package com.junoyi.system.controller;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.permission.enums.Logical;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统角色管理控制类
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysUserController.class);

    /**
     * 获取角色列表（分页）
     */
    @GetMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.role.view", "system.api.role.get"}
    )
    public void getRoleList(){

    }

    /**
     * 获取角色下拉列表选项
     */
    @GetMapping("/options")
    @Permission(
            value = {"system.ui.role.view", "system.api.role.get"}
    )
    public void getRoleOptions(){

    }

    /**
     * 通过id来获取角色
     */
    @GetMapping("/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.role.view", "system.api.role.get"}
    )
    public void getRoleById(@PathVariable("id") Long id){

    }

    /**
     * 添加角色
     */
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.role.view", "system.api.role.add"}
    )
    public void addRole(){

    }

    /**
     * 修改角色
     */
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.role.view", "system.api.role.update"}
    )
    public void updateRole(){

    }

    /**
     * 删除角色
     */
    @DeleteMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.role.view", "system.api.role.delete"}
    )
    public void delRole(){

    }
}