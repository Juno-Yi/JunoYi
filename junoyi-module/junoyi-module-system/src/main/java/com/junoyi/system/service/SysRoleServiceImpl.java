package com.junoyi.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.convert.SysRoleConverter;
import com.junoyi.system.domain.dto.SysRoleQueryDTO;
import com.junoyi.system.domain.po.SysRole;
import com.junoyi.system.domain.vo.SysRoleVo;
import com.junoyi.system.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * 系统角色业务接口实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements ISysRoleService{

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleConverter sysRoleConverter;

    /**
     * 查询角色列表
     * @param queryDTO 查询参数
     * @return 角色
     */
    @Override
    public PageResult<SysRoleVo> getRoleList(SysRoleQueryDTO queryDTO, Page<SysRole> page) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getRoleName()), SysRole::getRoleName, queryDTO.getRoleName())
               .like(StringUtils.hasText(queryDTO.getRoleKey()), SysRole::getRoleKey, queryDTO.getRoleKey())
               .eq(queryDTO.getStatus() != null, SysRole::getStatus, queryDTO.getStatus())
               .eq(queryDTO.getDelFlag() != null, SysRole::isDelFlag, queryDTO.getDelFlag())
               .orderByAsc(SysRole::getSort);

        Page<SysRole> resultPage = sysRoleMapper.selectPage(page, wrapper);
        return new PageResult<>(sysRoleConverter.toVoList(resultPage.getRecords()), resultPage.getTotal());
    }
}