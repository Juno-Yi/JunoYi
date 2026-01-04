package com.junoyi.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.exception.permission.PermGroupHasChildrenException;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.system.convert.SysPermGroupConverter;
import com.junoyi.system.domain.dto.SysPermGroupDTO;
import com.junoyi.system.domain.dto.SysPermGroupQueryDTO;
import com.junoyi.system.domain.po.SysPermGroup;
import com.junoyi.system.domain.vo.SysPermGroupVO;
import com.junoyi.system.mapper.SysPermGroupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限组服务实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysPermGroupServiceImpl implements ISysPermGroupService {

    private final SysPermGroupMapper sysPermGroupMapper;
    private final SysPermGroupConverter sysPermGroupConverter;

    /**
     * 获取权限组列表
     * @param queryDTO 查询条件DTO对象，包含权限组编码、名称、状态等查询条件
     * @param page 分页对象，包含分页参数
     * @return PageResult<SysPermGroupVO> 分页结果对象，包含权限组VO列表、总数、当前页码、每页大小
     */
    @Override
    public PageResult<SysPermGroupVO> getPermGroupList(SysPermGroupQueryDTO queryDTO, Page<SysPermGroup> page) {
        // 构建查询条件包装器
        LambdaQueryWrapper<SysPermGroup> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO != null) {
            // 根据查询条件构建动态SQL
            wrapper.like(StringUtils.hasText(queryDTO.getGroupCode()), SysPermGroup::getGroupCode, queryDTO.getGroupCode())
                    .like(StringUtils.hasText(queryDTO.getGroupName()), SysPermGroup::getGroupName, queryDTO.getGroupName())
                    .eq(queryDTO.getStatus() != null, SysPermGroup::getStatus, queryDTO.getStatus());
        }
        // 按优先级升序排序
        wrapper.orderByAsc(SysPermGroup::getPriority);

        Page<SysPermGroup> resultPage = sysPermGroupMapper.selectPage(page, wrapper);
        List<SysPermGroupVO> voList = sysPermGroupConverter.toVoList(resultPage.getRecords());
        
        // 填充父权限组名称
        fillParentName(voList);
        
        return PageResult.of(voList,
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize());
    }

    /**
     * 填充父权限组名称
     */
    private void fillParentName(List<SysPermGroupVO> voList) {
        if (voList == null || voList.isEmpty()) {
            return;
        }
        // 收集所有非空的 parentId
        Set<Long> parentIds = voList.stream()
                .map(SysPermGroupVO::getParentId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());
        
        if (parentIds.isEmpty()) {
            return;
        }
        
        // 批量查询父权限组
        LambdaQueryWrapper<SysPermGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysPermGroup::getId, parentIds)
                .select(SysPermGroup::getId, SysPermGroup::getGroupName);
        List<SysPermGroup> parents = sysPermGroupMapper.selectList(wrapper);
        
        // 构建 id -> name 映射
        Map<Long, String> parentNameMap = parents.stream()
                .collect(Collectors.toMap(SysPermGroup::getId, SysPermGroup::getGroupName));
        
        // 填充 parentName
        for (SysPermGroupVO vo : voList) {
            if (vo.getParentId() != null && vo.getParentId() > 0) {
                vo.setParentName(parentNameMap.get(vo.getParentId()));
            }
        }
    }

    /**
     * 获取权限组下拉列表（启用状态）
     * @return 权限组VO列表
     */
    @Override
    public List<SysPermGroupVO> getPermGroupOptions() {
        LambdaQueryWrapper<SysPermGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermGroup::getStatus, 1)
                .orderByAsc(SysPermGroup::getPriority);
        List<SysPermGroup> permGroups = sysPermGroupMapper.selectList(wrapper);
        return sysPermGroupConverter.toVoList(permGroups);
    }

    /**
     * 新增权限组
     * @param dto 权限组DTO对象，包含权限组的基本信息
     */
    @Override
    public void addPermGroup(SysPermGroupDTO dto) {
        // DTO转换为PO对象
        SysPermGroup permGroup = sysPermGroupConverter.toPo(dto);
        // 设置创建人和创建时间
        permGroup.setCreateBy(SecurityUtils.getUserName());
        permGroup.setCreateTime(DateUtils.getNowDate());
        sysPermGroupMapper.insert(permGroup);
    }

    /**
     * 更新权限组
     * @param dto 权限组DTO对象
     */
    @Override
    public void updatePermGroup(SysPermGroupDTO dto) {
        SysPermGroup permGroup = sysPermGroupConverter.toPo(dto);
        permGroup.setUpdateBy(SecurityUtils.getUserName());
        permGroup.setUpdateTime(DateUtils.getNowDate());
        sysPermGroupMapper.updateById(permGroup);
    }

    /**
     * 删除权限组
     * 如果存在子权限组则无法删除
     * @param id 权限组ID
     */
    @Override
    public void deletePermGroup(Long id) {
        // 检查是否存在子权限组
        LambdaQueryWrapper<SysPermGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermGroup::getParentId, id);
        Long childCount = sysPermGroupMapper.selectCount(wrapper);
        if (childCount > 0) {
            throw new PermGroupHasChildrenException("存在子权限组，无法删除");
        }
        // 物理删除
        sysPermGroupMapper.deleteById(id);
    }

}
