package com.junoyi.system.controller;

import com.junoyi.framework.log.core.JunoLog;
import com.junoyi.framework.log.core.JunoLogFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 角色控制类
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final JunoLog log = JunoLogFactory.getLogger(SysUserController.class);

    /**
     * 获取角色列表（分页）
     */
    @GetMapping
    public void getRoleList(){

    }

    /**
     * 获取角色下拉列表选项
     */
    @GetMapping("/options")
    public void getRoleOptions(){

    }

    /**
     * 通过id来获取角色
     */
    @GetMapping("/{id}")
    public void getRoleById(@PathVariable Long id){

    }

    /**
     * 添加角色
     */
    @PostMapping
    public void addRole(){

    }

    /**
     * 修改角色
     */
    @PutMapping
    public void updateRole(){

    }

    /**
     * 删除角色
     */
    @DeleteMapping
    public void delRole(){

    }
}