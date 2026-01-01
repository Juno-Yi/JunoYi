package com.junoyi.system.service;

import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.domain.dto.SysRoleQueryDTO;
import com.junoyi.system.domain.vo.SysRoleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * 系统角色业务接口实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements ISysRoleService{

    /**
     * 查询角色列表
     * @param queryDTO 查询参数
     * @return 角色
     */
    @Override
    public PageResult<SysRoleVo> getRoleList(SysRoleQueryDTO queryDTO) {
        return null;
    }
}