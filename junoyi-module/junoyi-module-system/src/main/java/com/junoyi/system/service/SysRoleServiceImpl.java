package com.junoyi.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.system.convert.SysPermGroupConverter;
import com.junoyi.system.convert.SysRoleConverter;
import com.junoyi.system.domain.dto.SysRoleDTO;
import com.junoyi.system.domain.dto.SysRoleQueryDTO;
import com.junoyi.system.domain.po.SysPermGroup;
import com.junoyi.system.domain.po.SysRole;
import com.junoyi.system.domain.po.SysRoleGroup;
import com.junoyi.system.domain.vo.SysPermGroupVO;
import com.junoyi.system.domain.vo.SysRoleVO;
import com.junoyi.system.enums.SysRoleStatus;
import com.junoyi.system.event.PermissionChangedEvent;
import com.junoyi.system.mapper.SysPermGroupMapper;
import com.junoyi.system.mapper.SysRoleGroupMapper;
import com.junoyi.system.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 系统角色业务接口实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements ISysRoleService{

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleGroupMapper sysRoleGroupMapper;
    private final SysPermGroupMapper sysPermGroupMapper;
    private final SysRoleConverter sysRoleConverter;
    private final SysPermGroupConverter sysPermGroupConverter;

    /**
     * 分页查询角色列表
     *
     * @param queryDTO 查询条件DTO
     * @param page 分页对象
     * @return 分页结果，包含角色VO列表、总数、当前页码、每页大小
     */
    @Override
    public PageResult<SysRoleVO> getRoleList(SysRoleQueryDTO queryDTO, Page<SysRole> page) {
        // 构建查询条件：根据角色名称、角色键、状态进行模糊查询，排除已删除记录，按排序字段升序排列
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getRoleName()), SysRole::getRoleName, queryDTO.getRoleName())
                .like(StringUtils.hasText(queryDTO.getRoleKey()), SysRole::getRoleKey, queryDTO.getRoleKey())
                .eq(queryDTO.getStatus() != null, SysRole::getStatus, queryDTO.getStatus())
                .eq(SysRole::isDelFlag, false)
                .orderByAsc(SysRole::getSort);

        Page<SysRole> resultPage = sysRoleMapper.selectPage(page, wrapper);
        return PageResult.of(sysRoleConverter.toVoList(resultPage.getRecords()),
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize());
    }

    /**
     * 获取所有可用角色列表（排除超级管理员）
     *
     * @return 角色VO列表
     */
    @Override
    public List<SysRoleVO> getRoleList() {
        // 构建查询条件：查询未删除且状态为启用的角色，排除超级管理员(ID=1)，按排序字段升序排列
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::isDelFlag, false)
                .eq(SysRole::getStatus, SysRoleStatus.ENABLE.getCode())
                .ne(SysRole::getId, 1L)
                .orderByAsc(SysRole::getSort);
        List<SysRole> sysRoles = sysRoleMapper.selectList(wrapper);
        return sysRoleConverter.toVoList(sysRoles);
    }

    /**
     * 根据ID获取角色信息
     *
     * @param id 角色ID
     * @return 角色VO对象
     */
    @Override
    public SysRoleVO getRoleById(Long id) {
        SysRole sysRole = sysRoleMapper.selectById(id);
        if (sysRole == null || sysRole.isDelFlag()) {
            return null;
        }
        return sysRoleConverter.toVo(sysRole);
    }

    /**
     * 新增角色
     *
     * @param roleDTO 角色DTO对象
     */
    @Override
    public void addRole(SysRoleDTO roleDTO) {
        SysRole sysRole = sysRoleConverter.toPo(roleDTO);
        sysRole.setStatus(SysRoleStatus.ENABLE.getCode());
        sysRole.setDelFlag(false);
        sysRole.setCreateBy(SecurityUtils.getUserName());
        sysRole.setCreateTime(DateUtils.getNowDate());
        sysRoleMapper.insert(sysRole);
    }

    /**
     * 更新角色信息
     *
     * @param roleDTO 角色DTO对象
     */
    @Override
    public void updateRole(SysRoleDTO roleDTO) {
        SysRole sysRole = sysRoleConverter.toPo(roleDTO);
        sysRole.setUpdateBy(SecurityUtils.getUserName());
        sysRole.setUpdateTime(DateUtils.getNowDate());
        sysRoleMapper.updateById(sysRole);
        
        // 发布角色权限变更事件
        EventBus.get().callEvent(new PermissionChangedEvent(
                PermissionChangedEvent.ChangeType.ROLE_PERM_UPDATE,
                roleDTO.getId()
        ));
    }

    /**
     * 逻辑删除角色
     *
     * @param id 角色ID
     */
    @Override
    public void deleteRole(Long id) {
        // 构建更新条件：根据ID将删除标志设置为true
        LambdaUpdateWrapper<SysRole> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysRole::getId, id)
                .set(SysRole::isDelFlag, true);
        sysRoleMapper.update(null, wrapper);
        
        // 发布角色删除事件
        EventBus.get().callEvent(new PermissionChangedEvent(
                PermissionChangedEvent.ChangeType.ROLE_DELETE,
                id
        ));
    }

    /**
     * 批量逻辑删除角色
     *
     * @param ids 角色ID列表
     */
    @Override
    public void deleteRoleBatch(List<Long> ids) {
        // 构建更新条件：根据ID列表将删除标志设置为true
        LambdaUpdateWrapper<SysRole> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysRole::getId, ids)
                .set(SysRole::isDelFlag, true);
        sysRoleMapper.update(null, wrapper);
        
        // 发布角色删除事件
        for (Long id : ids) {
            EventBus.get().callEvent(new PermissionChangedEvent(
                    PermissionChangedEvent.ChangeType.ROLE_DELETE,
                    id
            ));
        }
    }

    /**
     * 获取角色绑定的权限组列表
     *
     * @param roleId 角色ID
     * @return 权限组列表
     */
    @Override
    public List<SysPermGroupVO> getRolePermGroups(Long roleId) {
        // 查询角色权限组关联（只查未过期的）
        List<SysRoleGroup> roleGroups = sysRoleGroupMapper.selectList(
                new LambdaQueryWrapper<SysRoleGroup>()
                        .eq(SysRoleGroup::getRoleId, roleId)
                        .and(w -> w.isNull(SysRoleGroup::getExpireTime)
                                .or().gt(SysRoleGroup::getExpireTime, DateUtils.getNowDate())));

        if (roleGroups.isEmpty()) {
            return List.of();
        }

        // 获取权限组ID列表
        List<Long> groupIds = roleGroups.stream()
                .map(SysRoleGroup::getGroupId)
                .collect(Collectors.toList());

        // 查询权限组信息
        List<SysPermGroup> groups = sysPermGroupMapper.selectList(
                new LambdaQueryWrapper<SysPermGroup>()
                        .in(SysPermGroup::getId, groupIds));

        return sysPermGroupConverter.toVoList(groups);
    }

    /**
     * 更新角色权限组绑定
     * <p>
     * 优化策略：使用差量更新代替"先删后插"，减少锁竞争
     *
     * @param roleId 角色ID
     * @param groupIds 权限组ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRolePermGroups(Long roleId, List<Long> groupIds) {
        // 查询现有关联（只查未过期的）
        List<SysRoleGroup> existingGroups = sysRoleGroupMapper.selectList(
                new LambdaQueryWrapper<SysRoleGroup>()
                        .eq(SysRoleGroup::getRoleId, roleId)
                        .and(w -> w.isNull(SysRoleGroup::getExpireTime)
                                .or().gt(SysRoleGroup::getExpireTime, DateUtils.getNowDate())));
        Set<Long> existingGroupIds = existingGroups.stream()
                .map(SysRoleGroup::getGroupId).collect(Collectors.toSet());
        Set<Long> newGroupIds = (groupIds != null) ? new java.util.HashSet<>(groupIds) : Set.of();

        // 计算差量
        Set<Long> toDelete = existingGroupIds.stream()
                .filter(id -> !newGroupIds.contains(id)).collect(Collectors.toSet());
        Set<Long> toInsert = newGroupIds.stream()
                .filter(id -> !existingGroupIds.contains(id)).collect(Collectors.toSet());

        // 精确删除
        if (!toDelete.isEmpty()) {
            sysRoleGroupMapper.delete(new LambdaQueryWrapper<SysRoleGroup>()
                    .eq(SysRoleGroup::getRoleId, roleId)
                    .in(SysRoleGroup::getGroupId, toDelete));
        }

        // 批量插入
        Date now = DateUtils.getNowDate();
        for (Long groupId : toInsert) {
            SysRoleGroup roleGroup = new SysRoleGroup();
            roleGroup.setRoleId(roleId);
            roleGroup.setGroupId(groupId);
            roleGroup.setCreateTime(now);
            sysRoleGroupMapper.insert(roleGroup);
        }

        // 只有实际变更时才发布事件
        if (!toDelete.isEmpty() || !toInsert.isEmpty()) {
            EventBus.get().callEvent(new PermissionChangedEvent(
                    PermissionChangedEvent.ChangeType.ROLE_PERM_UPDATE,
                    roleId
            ));
        }
    }
}
