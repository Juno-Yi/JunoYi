package com.junoyi.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.security.utils.PasswordUtils;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.system.convert.SysRoleConverter;
import com.junoyi.system.convert.SysUserConverter;
import com.junoyi.system.domain.dto.SysUserDTO;
import com.junoyi.system.domain.dto.SysUserQueryDTO;
import com.junoyi.system.domain.po.SysRole;
import com.junoyi.system.domain.po.SysUser;
import com.junoyi.system.domain.po.SysUserDept;
import com.junoyi.system.domain.po.SysUserRole;
import com.junoyi.system.domain.vo.SysRoleVO;
import com.junoyi.system.domain.vo.SysUserVO;
import com.junoyi.system.enums.SysUserStatus;
import com.junoyi.system.mapper.SysRoleMapper;
import com.junoyi.system.mapper.SysUserDeptMapper;
import com.junoyi.system.mapper.SysUserMapper;
import com.junoyi.system.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统用户业务接口实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements ISysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserDeptMapper sysUserDeptMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserConverter sysUserConverter;
    private final SysRoleConverter sysRoleConverter;

    /**
     * 获取用户列表，支持分页和多条件查询
     *
     * @param queryDTO 查询条件DTO
     * @param page 分页对象
     * @return 分页结果对象，包含用户列表和分页信息
     */
    @Override
    public PageResult<SysUserVO> getUserList(SysUserQueryDTO queryDTO, Page<SysUser> page) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        // 如果有部门筛选条件，先查询该部门下的用户ID
        if (queryDTO.getDeptId() != null) {
            LambdaQueryWrapper<SysUserDept> deptWrapper = new LambdaQueryWrapper<>();
            deptWrapper.eq(SysUserDept::getDeptId, queryDTO.getDeptId());
            List<SysUserDept> userDeptList = sysUserDeptMapper.selectList(deptWrapper);
            List<Long> userIds = userDeptList.stream()
                    .map(SysUserDept::getUserId)
                    .collect(Collectors.toList());
            if (userIds.isEmpty()) {
                // 没有用户属于该部门，返回空结果
                return PageResult.of(List.of(), 0L, (int) page.getCurrent(), (int) page.getSize());
            }
            wrapper.in(SysUser::getUserId, userIds);
        }

        wrapper.like(StringUtils.hasText(queryDTO.getUserName()), SysUser::getUserName, queryDTO.getUserName())
                .like(StringUtils.hasText(queryDTO.getNickName()), SysUser::getNickName, queryDTO.getNickName())
                .like(StringUtils.hasText(queryDTO.getEmail()), SysUser::getEmail, queryDTO.getEmail())
                .like(StringUtils.hasText(queryDTO.getPhonenumber()), SysUser::getPhonenumber, queryDTO.getPhonenumber())
                .eq(StringUtils.hasText(queryDTO.getSex()), SysUser::getSex, queryDTO.getSex())
                .eq(queryDTO.getStatus() != null, SysUser::getStatus, queryDTO.getStatus())
                .eq(SysUser::isDelFlag, false);

        Page<SysUser> resultPage = sysUserMapper.selectPage(page, wrapper);
        return PageResult.of(
                sysUserConverter.toVoList(resultPage.getRecords()),
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize()
        );
    }

    /**
     * 新增用户
     *
     * @param userDTO 用户数据传输对象
     */
    @Override
    public void addUser(SysUserDTO userDTO) {
        // 创建用户实体
        SysUser sysUser = sysUserConverter.toEntity(userDTO);

        // 密码加密
        PasswordUtils.EncryptResult encryptResult = PasswordUtils.encrypt(userDTO.getPassword());
        sysUser.setPassword(encryptResult.getEncodedPassword());
        sysUser.setSalt(encryptResult.getSalt());

        // 设置默认值
        sysUser.setDelFlag(false);
        sysUser.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : SysUserStatus.NORMAL.getCode());
        sysUser.setCreateTime(DateUtils.getNowDate());
        sysUser.setCreateBy(SecurityUtils.getUserName());
        sysUser.setUpdateTime(DateUtils.getNowDate());
        sysUser.setUpdateBy(SecurityUtils.getUserName());

        // 插入用户
        sysUserMapper.insert(sysUser);
    }

    /**
     * 更新用户信息（不更新密码）
     *
     * @param userDTO 用户数据传输对象
     */
    @Override
    public void updateUser(SysUserDTO userDTO) {
        // 更新用户基本信息（不更新密码）
        SysUser sysUser = sysUserConverter.toEntity(userDTO);
        sysUser.setUserId(userDTO.getId());
        sysUser.setPassword(null);  // 不更新密码
        sysUser.setSalt(null);      // 不更新盐值
        sysUser.setUpdateTime(DateUtils.getNowDate());
        sysUser.setUpdateBy(SecurityUtils.getUserName());
        sysUserMapper.updateById(sysUser);
    }

    /**
     * 逻辑删除单个用户
     *
     * @param id 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        // 逻辑删除用户
        SysUser sysUser = new SysUser();
        sysUser.setUserId(id);
        sysUser.setDelFlag(true);
        sysUser.setUpdateTime(DateUtils.getNowDate());
        sysUser.setUpdateBy(SecurityUtils.getUserName());
        sysUserMapper.updateById(sysUser);

        // 删除用户角色关联
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, id));

        // 删除用户部门关联
        sysUserDeptMapper.delete(new LambdaQueryWrapper<SysUserDept>()
                .eq(SysUserDept::getUserId, id));
    }

    /**
     * 批量逻辑删除用户
     *
     * @param ids 用户ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        // 批量逻辑删除用户
        SysUser sysUser = new SysUser();
        sysUser.setDelFlag(true);
        sysUser.setUpdateTime(DateUtils.getNowDate());
        sysUser.setUpdateBy(SecurityUtils.getUserName());
        sysUserMapper.update(sysUser, new LambdaQueryWrapper<SysUser>()
                .in(SysUser::getUserId, ids));

        // 批量删除用户角色关联
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .in(SysUserRole::getUserId, ids));

        // 批量删除用户部门关联
        sysUserDeptMapper.delete(new LambdaQueryWrapper<SysUserDept>()
                .in(SysUserDept::getUserId, ids));
    }

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return 用户角色列表
     */
    @Override
    public List<SysRoleVO> getUserRoles(Long userId) {
        // 查询用户角色关联
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId));

        if (userRoles.isEmpty()) {
            return List.of();
        }

        // 获取角色ID列表
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 查询角色信息
        List<SysRole> roles = sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .in(SysRole::getId, roleIds)
                        .eq(SysRole::isDelFlag, false));

        return sysRoleConverter.toVoList(roles);
    }

    /**
     * 更新用户的角色关联
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRoles(Long userId, List<Long> roleIds) {
        // 先删除原有的用户角色关联
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));

        // 批量插入新的用户角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
    }
}
