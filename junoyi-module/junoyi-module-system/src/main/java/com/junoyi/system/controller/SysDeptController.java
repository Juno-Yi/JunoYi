package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.domain.dto.SysDeptDTO;
import com.junoyi.system.domain.dto.SysDeptQueryDTO;
import com.junoyi.system.domain.vo.SysDeptVO;
import com.junoyi.system.service.ISysDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统部门控制类
 *
 * @author Fan
 */
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class SysDeptController extends BaseController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysDeptController.class);

    private final ISysDeptService sysDeptService;

    /**
     * 获取部门树状列表（支持查询）
     */
    @GetMapping("/tree")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.dept.view", "system.api.dept.get"}
    )
    public R<List<SysDeptVO>> getDeptTree(SysDeptQueryDTO queryDTO){
        return R.ok(sysDeptService.getDeptTree(queryDTO));
    }

    /**
     * 获取部门详情
     */
    @GetMapping("/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.dept.view","system.api.dept.get"}
    )
    public R<SysDeptVO> getDeptById(@PathVariable("id") Long id){
        return R.ok(sysDeptService.getDeptById(id));
    }

    /**
     * 添加部门
     */
    @PostMapping()
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.dept.view", "system.api.dept.add"}
    )
    public R<Void> addDept(@RequestBody SysDeptDTO sysDeptDTO){
        sysDeptService.addDept(sysDeptDTO);
        return R.ok();
    }

    /**
     * 更新部门
     */
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.dept.view", "system.api.dept.update"}
    )
    public R<Void> updateDept(@RequestBody SysDeptDTO sysDeptDTO){
        sysDeptService.updateDept(sysDeptDTO);
        return R.ok();
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(
            value = {"system.ui.dept.view","system.api.dept.delete"}
    )
    public R<Void> deleteDept(@PathVariable("id") Long id) {
        sysDeptService.deleteDept(id);
        return R.ok();
    }
}