package com.junoyi.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.junoyi.system.exception.DeptHasChildrenException;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.system.convert.SysDeptConverter;
import com.junoyi.system.convert.SysPermGroupConverter;
import com.junoyi.system.domain.bo.SysDeptSortItem;
import com.junoyi.system.domain.dto.SysDeptDTO;
import com.junoyi.system.domain.dto.SysDeptQueryDTO;
import com.junoyi.system.domain.dto.SysDeptSortDTO;
import com.junoyi.system.domain.po.SysDept;
import com.junoyi.system.domain.po.SysDeptGroup;
import com.junoyi.system.domain.po.SysPermGroup;
import com.junoyi.system.domain.vo.SysDeptVO;
import com.junoyi.system.domain.vo.SysPermGroupVO;
import com.junoyi.system.enums.SysDeptStatus;
import com.junoyi.system.event.PermissionChangedEvent;
import com.junoyi.system.mapper.SysDeptGroupMapper;
import com.junoyi.system.mapper.SysDeptMapper;
import com.junoyi.system.mapper.SysPermGroupMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统部门业务接口实现类
 *
 * @author Fan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl implements ISysDeptService {

    private final SysDeptMapper sysDeptMapper;
    private final SysDeptGroupMapper sysDeptGroupMapper;
    private final SysPermGroupMapper sysPermGroupMapper;
    private final SysDeptConverter sysDeptConverter;
    private final SysPermGroupConverter sysPermGroupConverter;

    /**
     * 获取部门树形结构数据
     * 根据查询条件构建部门树，支持按名称、负责人、电话、邮箱、状态等条件进行筛选
     *
     * @param queryDTO 部门查询条件对象，包含名称、负责人、电话、邮箱、状态等筛选条件
     * @return 部门树形结构VO列表，按排序字段升序排列
     */
    @Override
    public List<SysDeptVO> getDeptTree(SysDeptQueryDTO queryDTO) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getName()), SysDept::getName, queryDTO.getName())
                .like(StringUtils.hasText(queryDTO.getLeader()), SysDept::getLeader, queryDTO.getLeader())
                .like(StringUtils.hasText(queryDTO.getPhonenumber()), SysDept::getPhonenumber, queryDTO.getPhonenumber())
                .like(StringUtils.hasText(queryDTO.getEmail()), SysDept::getEmail, queryDTO.getEmail())
                .eq(queryDTO.getStatus() != null, SysDept::getStatus, queryDTO.getStatus())
                .eq(SysDept::isDelFlag, false)
                .orderByAsc(SysDept::getSort);

        // 查询符合条件的部门列表
        List<SysDept> deptList = sysDeptMapper.selectList(wrapper);
        // 将实体对象转换为VO对象
        List<SysDeptVO> voList = sysDeptConverter.toVoList(deptList);
        // 构建树形结构并返回
        return buildTree(voList);
    }

    /**
     * 根据ID获取部门信息
     * 查询指定ID的部门信息，如果部门不存在或已被删除则返回null
     *
     * @param id 部门ID
     * @return 部门VO对象，如果部门不存在或已被删除则返回null
     */
    @Override
    public SysDeptVO getDeptById(Long id) {
        SysDept sysDept = sysDeptMapper.selectById(id);
        if (sysDept == null || sysDept.isDelFlag()) {
            return null;
        }
        return sysDeptConverter.toVo(sysDept);
    }

    /**
     * 添加部门
     *
     * @param deptDTO 部门信息
     */
    @Override
    public void addDept(SysDeptDTO deptDTO) {
        SysDept sysDept = sysDeptConverter.toPo(deptDTO);
        sysDept.setDelFlag(false);
        sysDept.setStatus(SysDeptStatus.ENABLE.getCode());
        sysDept.setCreateTime(DateUtils.getNowDate());
        sysDept.setCreateBy(SecurityUtils.getUserName());
        sysDeptMapper.insert(sysDept);
    }

    /**
     * 更新部门
     *
     * @param deptDTO 部门信息
     */
    @Override
    public void updateDept(SysDeptDTO deptDTO) {
        SysDept sysDept = sysDeptConverter.toPo(deptDTO);
        sysDept.setUpdateTime(DateUtils.getNowDate());
        sysDept.setUpdateBy(SecurityUtils.getUserName());
        sysDeptMapper.updateById(sysDept);
    }

    /**
     * 删除部门（逻辑删除）
     * 如果存在子部门则无法删除
     *
     * @param id 部门ID
     */
    @Override
    public void deleteDept(Long id) {
        // 检查是否存在子部门
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, id)
               .eq(SysDept::isDelFlag, false);
        Long childCount = sysDeptMapper.selectCount(wrapper);
        if (childCount > 0) {
            throw new DeptHasChildrenException("存在子部门，无法删除");
        }
        // 逻辑删除
        SysDept sysDept = new SysDept();
        sysDept.setId(id);
        sysDept.setDelFlag(true);
        sysDept.setUpdateTime(DateUtils.getNowDate());
        sysDept.setUpdateBy(SecurityUtils.getUserName());
        sysDeptMapper.updateById(sysDept);
    }

    /**
     * 构建部门树
     */
    private List<SysDeptVO> buildTree(List<SysDeptVO> deptList) {
        Map<Long, SysDeptVO> deptMap = deptList.stream()
                .collect(Collectors.toMap(SysDeptVO::getId, dept -> dept));

        List<SysDeptVO> rootList = new ArrayList<>();
        for (SysDeptVO dept : deptList) {
            Long parentId = dept.getParentId();
            if (parentId == null || parentId == 0L) {
                rootList.add(dept);
            } else {
                SysDeptVO parent = deptMap.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(dept);
                } else {
                    // 父节点不存在时作为根节点
                    rootList.add(dept);
                }
            }
        }
        return rootList;
    }

    /**
     * 批量部门排序
     * @param sortList 排序列表
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDeptSort(List<SysDeptSortItem> sortList) {
        if (sortList == null || sortList.isEmpty()){
            return false;
        }
        log.debug("批量更新部门排序，数量: {}", sortList.size());

        for (SysDeptSortItem item : sortList){
            if (item.getId() == null){
                continue;
            }
            // 使用 LambdaUpdateWrapper 只更新指定字段，避免基本类型默认值覆盖
            LambdaUpdateWrapper<SysDept> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SysDept::getId, item.getId())
                    .set(SysDept::getParentId, item.getParentId())
                    .set(item.getSort() != null, SysDept::getSort, item.getSort())
                    .set(SysDept::getUpdateTime, DateUtils.getNowDate())
                    .set(SysDept::getUpdateBy, SecurityUtils.getUserName());
            sysDeptMapper.update(null, updateWrapper);
        }

        return true;
    }

    /**
     * 获取部门绑定的权限组列表
     *
     * @param deptId 部门ID
     * @return 权限组列表
     */
    @Override
    public List<SysPermGroupVO> getDeptPermGroups(Long deptId) {
        // 查询部门权限组关联（只查未过期的）
        List<SysDeptGroup> deptGroups = sysDeptGroupMapper.selectList(
                new LambdaQueryWrapper<SysDeptGroup>()
                        .eq(SysDeptGroup::getDeptId, deptId)
                        .and(w -> w.isNull(SysDeptGroup::getExpireTime)
                                .or().gt(SysDeptGroup::getExpireTime, DateUtils.getNowDate())));

        if (deptGroups.isEmpty()) {
            return List.of();
        }

        // 获取权限组ID列表
        List<Long> groupIds = deptGroups.stream()
                .map(SysDeptGroup::getGroupId)
                .collect(Collectors.toList());

        // 查询权限组信息
        List<SysPermGroup> groups = sysPermGroupMapper.selectList(
                new LambdaQueryWrapper<SysPermGroup>()
                        .in(SysPermGroup::getId, groupIds));

        return sysPermGroupConverter.toVoList(groups);
    }

    /**
     * 更新部门权限组绑定
     *
     * @param deptId 部门ID
     * @param groupIds 权限组ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDeptPermGroups(Long deptId, List<Long> groupIds) {
        // 先删除原有的部门权限组关联
        sysDeptGroupMapper.delete(new LambdaQueryWrapper<SysDeptGroup>()
                .eq(SysDeptGroup::getDeptId, deptId));

        // 批量插入新的部门权限组关联
        if (groupIds != null && !groupIds.isEmpty()) {
            for (Long groupId : groupIds) {
                SysDeptGroup deptGroup = new SysDeptGroup();
                deptGroup.setDeptId(deptId);
                deptGroup.setGroupId(groupId);
                deptGroup.setCreateTime(DateUtils.getNowDate());
                sysDeptGroupMapper.insert(deptGroup);
            }
        }

        // 发布部门权限组变更事件，同步该部门下用户的会话
        EventBus.get().callEvent(new PermissionChangedEvent(
                PermissionChangedEvent.ChangeType.DEPT_GROUP_CHANGE,
                deptId
        ));
    }
}