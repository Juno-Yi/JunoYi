package com.junoyi.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
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
        return PageResult.of(sysPermGroupConverter.toVoList(resultPage.getRecords()),
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize());
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

}
