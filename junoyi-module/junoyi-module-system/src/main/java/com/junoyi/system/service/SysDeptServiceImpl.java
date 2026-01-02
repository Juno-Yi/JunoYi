package com.junoyi.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.core.exception.dept.DeptHasChildrenException;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.system.convert.SysDeptConverter;
import com.junoyi.system.domain.dto.SysDeptDTO;
import com.junoyi.system.domain.dto.SysDeptQueryDTO;
import com.junoyi.system.domain.po.SysDept;
import com.junoyi.system.domain.vo.SysDeptVO;
import com.junoyi.system.enums.SysDeptStatus;
import com.junoyi.system.mapper.SysDeptMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl implements ISysDeptService {

    private final SysDeptMapper sysDeptMapper;
    private final SysDeptConverter sysDeptConverter;

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
}