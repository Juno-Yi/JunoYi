package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.permission.enums.Logical;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.domain.dto.SysUserDTO;
import com.junoyi.system.domain.dto.SysUserQueryDTO;
import com.junoyi.system.domain.vo.SysUserVO;
import com.junoyi.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户控制类
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController extends BaseController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysUserController.class);
    private final ISysUserService sysUserService;

    /**
     * 获取用户列表（分页）
     * @return 响应结果
     */
    @GetMapping
    @Permission(
            value = {"system.ui.user.view", "system.api.user.get"}
    )
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<PageResult<SysUserVO>> getUserList(SysUserQueryDTO queryDTO){
        return R.ok(sysUserService.getUserList(queryDTO, buildPage()));
    }

    /**
     * 通过 id 来获取用户
     * @param id 用户 id
     * @return 响应结果
     */
    @GetMapping("/{id}")
    @Permission("system.user.data.id")
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<?> getUserById(@PathVariable Long id){
        return R.ok(id);
    }


    /**
     * 添加用户
     */
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.user.view", "system.api.user.add"}
    )
    public R<Void> addUser(@RequestBody SysUserDTO sysUserDTO){
        sysUserService.addUser(sysUserDTO);
        return R.ok();
    }


    /**
     * 更新用户
     */
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.user.view", "system.api.user.update"}
    )
    public R<Void> updateUser(@RequestBody SysUserDTO sysUserDTO){
        sysUserService.updateUser(sysUserDTO);
        return R.ok();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.user.view", "system.api.user.delete"}
    )
    public R<Void> deleteUser(@PathVariable("id") Long id){
        sysUserService.deleteUser(id);
        return R.ok();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.user.view", "system.api.user.delete"}
    )
    public R<Void> deleteUserBatch(@RequestBody List<Long> ids){
        sysUserService.deleteUserBatch(ids);
        return R.ok();
    }

}