package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/permission")
@RequiredArgsConstructor
public class SysPermissionController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysPermissionController.class);


    public R<?> getSystemPermissionList(){

        return R.ok();
    }

    public R<?> getSystemPermissionOptions(){
        return R.ok();
    }

}