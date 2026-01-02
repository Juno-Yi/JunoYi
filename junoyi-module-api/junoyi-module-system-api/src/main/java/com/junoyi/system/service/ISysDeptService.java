package com.junoyi.system.service;

import com.junoyi.system.domain.dto.SysDeptDTO;
import com.junoyi.system.domain.dto.SysDeptQueryDTO;
import com.junoyi.system.domain.vo.SysDeptVO;

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
     * 删除部门（逻辑删除）
     * @param id 部门ID
     */
    void deleteDept(Long id);
}