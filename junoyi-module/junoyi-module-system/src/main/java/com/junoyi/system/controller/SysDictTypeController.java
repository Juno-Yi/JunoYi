package com.junoyi.system.controller;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.web.domain.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 系统字典类型控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/dict/type")
@RequiredArgsConstructor
public class SysDictTypeController extends BaseController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysDictTypeController.class);
}