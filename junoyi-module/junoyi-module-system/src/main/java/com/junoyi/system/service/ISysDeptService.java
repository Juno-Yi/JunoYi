package com.junoyi.system.service;

import com.junoyi.system.domain.bo.SysDeptSortItem;
import com.junoyi.system.domain.dto.SysDeptDTO;
import com.junoyi.system.domain.dto.SysDeptQueryDTO;
import com.junoyi.system.domain.vo.SysDeptVO;
import com.junoyi.system.domain.vo.SysPermGroupVO;

import java.util.List;

/**
 * 系统部门业务接口类
 *
 * @author Fan
 */
public interface ISysDeptService {

    /**
     * 获取部门树状列表
     * @param queryDTO 查询条件
     * @return 部门树
     */
    List<SysDeptVO> getDeptTree(SysDeptQueryDTO queryDTO);

    /**
     * 获取用户管理使用的启用部门树，禁用节点及其子树不返回。
     */
    List<SysDeptVO> getEnabledDeptTree();

    /**
     * 根据ID获取部门详情
     * @param id 部门ID
     * @return 部门信息
     */
    SysDeptVO getDeptById(Long id);

    /**
     * 添加部门
     * @param deptDTO 部门信息
     */
    void addDept(SysDeptDTO deptDTO);

    /**
     * 更新部门
     * @param deptDTO 部门信息
     */
    void updateDept(SysDeptDTO deptDTO);

    /**
     * 批量更新部门排序
     * @param sortList 排序列表
     * @return 是否成功
     */
    boolean updateDeptSort(List<SysDeptSortItem> sortList);

    /**
     * 删除部门（逻辑删除）
     * @param id 部门ID
     */
    void deleteDept(Long id);

    /**
     * 获取部门绑定的权限组列表
     * @param deptId 部门ID
     * @return 权限组列表
     */
    List<SysPermGroupVO> getDeptPermGroups(Long deptId);

    /**
     * 更新部门权限组绑定
     * @param deptId 部门ID
     * @param groupIds 权限组ID列表
     */
    void updateDeptPermGroups(Long deptId, List<Long> groupIds);
}