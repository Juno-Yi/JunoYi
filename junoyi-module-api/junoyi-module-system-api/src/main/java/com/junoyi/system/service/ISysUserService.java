package com.junoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.domain.dto.SysUserDTO;
import com.junoyi.system.domain.dto.SysUserQueryDTO;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.vo.SysRoleVO;
import com.junoyi.system.domain.vo.SysUserVO;

import java.util.List;

/**
 * 系统用户业务接口类
 *
 * @author Fan
 */
public interface ISysUserService {

    /**
     * 获取用户列表（分页）
     * @param queryDTO 查询条件
     * @param page 分页参数
     * @return 用户分页列表
     */
    PageResult<SysUserVO> getUserList(SysUserQueryDTO queryDTO, Page<SysUser> page);

    /**
     * 添加用户
     * @param userDTO 用户信息
     */
    void addUser(SysUserDTO userDTO);

    /**
     * 更新用户（不更新密码）
     * @param userDTO 用户信息
     */
    void updateUser(SysUserDTO userDTO);

    /**
     * 删除用户（逻辑删除）
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 批量删除用户（逻辑删除）
     * @param ids 用户ID列表
     */
    void deleteUserBatch(List<Long> ids);

    /**
     * 获取用户绑定的角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRoleVO> getUserRoles(Long userId);

    /**
     * 更新用户角色绑定
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void updateUserRoles(Long userId, List<Long> roleIds);
}
