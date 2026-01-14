package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.domain.dto.SysSessionQueryDTO;
import com.junoyi.system.domain.po.SysSession;
import com.junoyi.system.service.ISysSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统会话控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/session")
@RequiredArgsConstructor
public class SysSessionController extends BaseController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysSessionController.class);

    private final ISysSessionService sysSessionService;

    /**
     * 获取会话列表（分页）
     */
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.session.view", "system.api.session.get.list"}
    )
    public R<PageResult<SysSession>> getSessionList(SysSessionQueryDTO queryDTO) {
        return R.ok(sysSessionService.getSessionList(queryDTO, getPageQuery()));
    }

    /**
     * 踢出指定会话
     */
    @DeleteMapping("/{sessionId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.session.view", "system.api.session.logout.id"}
    )
    public R<Void> kickOut(@PathVariable("sessionId") String sessionId) {
        sysSessionService.kickOut(sessionId);
        return R.ok();
    }

    /**
     * 批量踢出会话
     * 
     * @param sessionIds 会话ID列表
     */
    @DeleteMapping("/batch")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.session.view", "system.api.session.logout.batcha"}
    )
    public R<Void> kickOutBatch(@RequestBody List<String> sessionIds) {
        sysSessionService.kickOutBatch(sessionIds);
        return R.ok();
    }
}