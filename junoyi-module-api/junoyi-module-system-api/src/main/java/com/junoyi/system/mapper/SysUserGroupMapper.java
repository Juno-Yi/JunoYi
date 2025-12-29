package com.junoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.system.domain.po.SysUserGroup;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户权限组关联 Mapper
 *
 * @author Fan
 */
@Mapper
public interface SysUserGroupMapper extends BaseMapper<SysUserGroup> {
}
