package com.junoyi.demo.controller;

import com.junoyi.demo.event.TestEvent;
import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
public class TestController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(TestController.class);

    @GetMapping("/event")
    public void testEvent(){
        EventBus.get().callEvent(new TestEvent("测试事件"));
    }

    /**
     * 只允许 网页端使用 (后台管理web、 前台用户web)
     */
    @PlatformScope( {PlatformType.ADMIN_WEB, PlatformType.FRONT_DESK_WEB})
    @GetMapping("/web")
    public R<String> helloWorld(){
        return R.ok("Hello World");
    }

    /**
     * 只允许小程序和APP使用
     */
    @PlatformScope( {PlatformType.MINI_PROGRAM, PlatformType.APP} )
    @GetMapping("/app")
    public R<String> helloWorldApp(){
        return R.ok();
    }

    /**
     * 只允许桌面端使用
     */
    @PlatformScope( {PlatformType.DESKTOP_APP, PlatformType.ADMIN_WEB} )
    @GetMapping("/desktop")
    public R<String> helloWorldDesktop() {
        return R.ok();
    }

    @GetMapping("/xss")
    public R<?> testXss(){
        return R.ok();
    }

    @PostMapping("/xss")
    public R<?> testXss2(@RequestParam Long id){
        return R.ok();
    }

    @GetMapping("/permission")
    @Permission("system.demo.permission")
    @PlatformScope(PlatformType.APP)
    public R<String> testPermission(){
        return R.ok("Hello World");
    }
}