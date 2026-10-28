package com.junoyi.system.service.impl;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.constant.DictTypeConstants;
import com.junoyi.system.domain.dto.SysAuthLogQueryDTO;
import com.junoyi.system.domain.po.SysAuthLog;
import com.junoyi.system.domain.vo.SysAuthLogVO;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.mapper.SysAuthLogMapper;
import com.junoyi.system.service.ISysAuthLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Map;

/**
 * 系统登录日志业务接口实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysAuthLogServiceImpl implements ISysAuthLogService {

    private final SysAuthLogMapper sysAuthLogMapper;
    private final SysDictApi sysDictApi;

    /**
     * 分页查询登录日志
     *
     * @param queryDTO 查询条件
     * @param page     分页参数
     * @return 分页结果
     */
    @Override
    public PageResult<SysAuthLogVO> getLoginLogList(SysAuthLogQueryDTO queryDTO, Page<SysAuthLog> page) {
        LambdaQueryWrapper<SysAuthLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getUserName()), SysAuthLog::getUserName, queryDTO.getUserName())
                .like(StringUtils.hasText(queryDTO.getNickName()), SysAuthLog::getNickName, queryDTO.getNickName())
                .like(StringUtils.hasText(queryDTO.getLoginIp()), SysAuthLog::getLoginIp, queryDTO.getLoginIp())
                .eq(StringUtils.hasText(queryDTO.getLoginType()), SysAuthLog::getLoginType, queryDTO.getLoginType())
                .eq(queryDTO.getStatus() != null, SysAuthLog::getStatus, queryDTO.getStatus())
                .ge(queryDTO.getStartTime() != null, SysAuthLog::getLoginTime, queryDTO.getStartTime())
                .le(queryDTO.getEndTime() != null, SysAuthLog::getLoginTime, queryDTO.getEndTime())
                .orderByDesc(SysAuthLog::getLoginTime);
        Page<SysAuthLog> resultPage = sysAuthLogMapper.selectPage(page, wrapper);
        Map<String, SysDictDataVO> loginTypes = sysDictApi
                .getDictItemMaps(List.of(DictTypeConstants.SYS_LOGIN_TYPE))
                .getOrDefault(DictTypeConstants.SYS_LOGIN_TYPE, Map.of());

        List<SysAuthLogVO> voList = resultPage.getRecords().stream()
                .map(log -> convertToVO(log, loginTypes))
                .collect(Collectors.toList());

        return PageResult.of(voList,
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize());
    }

    /**
     * 记录登录日志
     *
     * @param authLog 登录日志
     */
    @Override
    public void recordLoginLog(SysAuthLog authLog) {
        sysAuthLogMapper.insert(authLog);
    }

    /**
     * 清空登录日志
     */
    @Override
    @InterceptorIgnore(blockAttack = "true")
    public void clearLoginLog() {
        sysAuthLogMapper.truncate();
    }

    /**
     * 删除登录日志
     *
     * @param ids 日志ID数组
     */
    @Override
    public void deleteLoginLog(Long[] ids) {
        sysAuthLogMapper.deleteBatchIds(Arrays.asList(ids));
    }


    private SysAuthLogVO convertToVO(SysAuthLog loginLog, Map<String, SysDictDataVO> loginTypes) {
        SysAuthLogVO vo = new SysAuthLogVO();
        BeanUtils.copyProperties(loginLog, vo);

        String loginType = loginLog.getLoginType();
        SysDictDataVO loginTypeDict = loginTypes.get(loginType);
        vo.setLoginTypeName(loginTypeDict != null ? loginTypeDict.getDictLabel() : loginType);
        return vo;
    }
}
