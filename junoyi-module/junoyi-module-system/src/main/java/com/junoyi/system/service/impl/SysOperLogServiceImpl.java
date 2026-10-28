package com.junoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.utils.ServletUtils;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.constant.DictTypeConstants;
import com.junoyi.system.domain.dto.SysOperLogQueryDTO;
import com.junoyi.system.domain.po.SysOperLog;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.domain.vo.SysOperLogVO;
import com.junoyi.system.mapper.SysOperLogMapper;
import com.junoyi.system.service.ISysOperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统操作日志业务接口实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysOperLogServiceImpl implements ISysOperLogService {

    private final SysOperLogMapper sysOperLogMapper;
    private final SysDictApi sysDictApi;

    /**
     * 分页查询操作日志
     *
     * @param queryDTO 查询条件
     * @param page     分页参数
     * @return 分页结果
     */
    @Override
    public PageResult<SysOperLogVO> getOperationLogList(SysOperLogQueryDTO queryDTO, Page<SysOperLog> page) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(queryDTO.getLevel()), SysOperLog::getLevel, queryDTO.getLevel())
                .eq(StringUtils.hasText(queryDTO.getAction()), SysOperLog::getAction, queryDTO.getAction())
                .eq(StringUtils.hasText(queryDTO.getModule()), SysOperLog::getModule, queryDTO.getModule())
                .like(StringUtils.hasText(queryDTO.getUserName()), SysOperLog::getUserName, queryDTO.getUserName())
                .eq(StringUtils.hasText(queryDTO.getTargetId()), SysOperLog::getTargetId, queryDTO.getTargetId())
                .like(StringUtils.hasText(queryDTO.getMessage()), SysOperLog::getMessage, queryDTO.getMessage())
                .ge(StringUtils.hasText(queryDTO.getStartTime()), SysOperLog::getCreateTime, queryDTO.getStartTime())
                .le(StringUtils.hasText(queryDTO.getEndTime()), SysOperLog::getCreateTime, queryDTO.getEndTime())
                .orderByDesc(SysOperLog::getCreateTime);

        Page<SysOperLog> resultPage = sysOperLogMapper.selectPage(page, wrapper);
        Map<String, Map<String, SysDictDataVO>> dictItems = sysDictApi.getDictItemMaps(List.of(
                DictTypeConstants.SYS_LOG_LEVEL, DictTypeConstants.SYS_OPER_TYPE,
                DictTypeConstants.SYS_OPER_MODULE));

        List<SysOperLogVO> voList = resultPage.getRecords().stream()
                .map(log -> convertToVO(log, dictItems))
                .collect(Collectors.toList());

        return PageResult.of(voList,
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize());
    }

    /**
     * 清空操作日志
     */
    @Override
    public void clearOperationLog() {
        sysOperLogMapper.truncate();
    }


    /**
     * 删除操作日志
     *
     * @param ids 日志ID数组
     */
    @Override
    public void deleteOperationLog(Long[] ids) {
        sysOperLogMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 记录操作日志
     *
     * @param operationLog 操作日志
     */
    @Override
    @Async
    public void recordOperationLog(SysOperLog operationLog) {
        sysOperLogMapper.insert(operationLog);
    }

    /**
     * 记录操作日志（简化版）
     *
     * @param level      日志级别
     * @param action     动作
     * @param module     模块
     * @param message    详情描述
     * @param targetId   对象ID
     * @param targetName 对象名称
     * @param rawData    原始数据
     */
    @Override
    @Async
    public void recordLog(String level, String action, String module, String message, String targetId, String targetName, String rawData) {
        SysOperLog log = new SysOperLog();
        log.setLevel(level);
        log.setAction(action);
        log.setModule(module);
        log.setMessage(message);
        log.setTargetId(targetId);
        log.setTargetName(targetName);
        log.setRawData(rawData);
        log.setCreateTime(new Date());

        // 获取当前用户信息
        try {
            log.setUserId(SecurityUtils.getUserId());
            log.setUserName(SecurityUtils.getUserName());
            log.setNickName(SecurityUtils.getNickName());
        } catch (Exception ignored) {
            // 未登录情况下忽略
        }

        // 获取请求信息
        try {
            log.setIp(ServletUtils.getClientIp());
            log.setPath(ServletUtils.getRequest().getRequestURI());
            log.setMethod(ServletUtils.getRequest().getMethod());
        } catch (Exception ignored) {
            // 非HTTP请求上下文忽略
        }

        sysOperLogMapper.insert(log);
    }

    /**
     * 记录操作日志（简化版-info级别）
     */
    @Override
    @Async
    public void recordInfoLog(String action, String module, String message, String targetId, String targetName) {
        recordLog("info", action, module, message, targetId, targetName, null);
    }


    private SysOperLogVO convertToVO(SysOperLog log,
                                     Map<String, Map<String, SysDictDataVO>> dictItems) {
        SysOperLogVO vo = new SysOperLogVO();
        BeanUtils.copyProperties(log, vo);

        // 使用字典翻译日志级别
        if (log.getLevel() != null) {
            SysDictDataVO levelItem = dictItems.getOrDefault(DictTypeConstants.SYS_LOG_LEVEL, Map.of()).get(log.getLevel());
            String levelLabel = levelItem != null ? levelItem.getDictLabel() : null;
            vo.setLevelLabel(levelLabel);
        }

        // 使用字典翻译操作类型
        if (log.getAction() != null) {
            SysDictDataVO actionItem = dictItems.getOrDefault(DictTypeConstants.SYS_OPER_TYPE, Map.of()).get(log.getAction());
            String actionLabel = actionItem != null ? actionItem.getDictLabel() : null;
            vo.setActionLabel(actionLabel);
        }

        // 使用字典翻译操作模块
        if (log.getModule() != null) {
            SysDictDataVO moduleItem = dictItems.getOrDefault(DictTypeConstants.SYS_OPER_MODULE, Map.of()).get(log.getModule());
            String moduleLabel = moduleItem != null ? moduleItem.getDictLabel() : null;
            vo.setModuleLabel(moduleLabel);
        }

        return vo;
    }
}
