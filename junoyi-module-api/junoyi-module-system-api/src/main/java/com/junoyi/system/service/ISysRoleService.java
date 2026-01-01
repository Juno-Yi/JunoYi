package com.junoyi.system.service;

import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.domain.dto.SysRoleQueryDTO;
import com.junoyi.system.domain.vo.SysRoleVo;


/**
 * 系统角色业务接口类
 *
 * @author Fan
 */
public interface ISysRoleService {

    /**
     * 查询角色列表
     * @param queryDTO 查询参数
     * @return 角色
     */
    PageResult<SysRoleVo> getRoleList(SysRoleQueryDTO queryDTO);
}
