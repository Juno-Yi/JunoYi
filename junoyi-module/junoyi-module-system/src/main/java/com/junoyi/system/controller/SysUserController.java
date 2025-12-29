package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.permission.enums.Logical;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统用户控制类
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController {
    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysUserController.class);

    /**
     * 获取用户列表（分页）
     * @return 响应结果
     */
    @GetMapping
    @Permission(
            // 需要拥有什么权限
            value = {"system.user.data.list", "system.user.ui.view"},
            // 权限逻辑计算（OR两者拥有一个，AND两者必须都要有）
            logical = Logical.OR
    )
    @PlatformScope(PlatformType.ADMIN_WEB)
    public R<?> getUserList(){

        return R.ok();
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
    public void addUser(){

    }

    /**
     * 删除用户
     */
    @DeleteMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    public void delUser(){

    }

    /**
     * 删除用户
     */
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    public void updateUser(){
    }
}