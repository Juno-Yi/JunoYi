package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.R;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public R<?> getUserList(){

        return R.ok();
    }

    /**
     * 通过id来获取用户
     * @param id 用户id
     * @return 响应结果
     */
    @GetMapping("/{id}")
    public R<?> getUserById(@PathVariable Long id){
        return R.ok(id);
    }

    /**
     * 添加用户
     */
    @PostMapping
    public void addUser(){

    }

    /**
     * 删除用户
     */
    @DeleteMapping
    public void delUser(){

    }

    /**
     * 删除用户
     */
    @PutMapping
    public void updateUser(){
    }
}