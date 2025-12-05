package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.R;
import com.junoyi.framework.log.core.JunoLog;
import com.junoyi.framework.log.core.JunoLogFactory;
import com.junoyi.framework.stater.config.JunoYiProperties;
import com.junoyi.system.domain.vo.SystemInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统信息控制类
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/info")
@RequiredArgsConstructor
public class SysInfoController {

    private final JunoLog log = JunoLogFactory.getLogger(SysInfoController.class);

    private final JunoYiProperties junoYiProperties;

    /**
     * 获取系统信息
     */
    @GetMapping
    public R<SystemInfoVo> getSystemInfo(){
        return R.ok();
    }
}