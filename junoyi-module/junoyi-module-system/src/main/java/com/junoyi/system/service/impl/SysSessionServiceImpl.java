package com.junoyi.system.service.impl;

import com.junoyi.framework.core.domain.page.PageQuery;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.security.helper.SessionHelper;
import com.junoyi.framework.security.module.UserSession;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.constant.DictTypeConstants;
import com.junoyi.system.domain.dto.SysSessionQueryDTO;
import com.junoyi.system.domain.po.SysSession;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.service.ISysSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统会话服务实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysSessionServiceImpl implements ISysSessionService {

    private final SessionHelper sessionHelper;
    private final SysDictApi sysDictApi;

    /**
     * 获取系统会话列表，支持查询条件过滤和分页
     *
     * @param queryDTO 查询条件对象，用于过滤会话数据
     * @param pageQuery 分页查询对象，包含页码和每页大小信息
     * @return PageResult<SysSession> 包含分页结果的会话列表，包括当前页数据、总数、页码和页面大小
     */
    @Override
    public PageResult<SysSession> getSessionList(SysSessionQueryDTO queryDTO, PageQuery pageQuery) {
        // 获取所有会话
        List<UserSession> allSessions = sessionHelper.getAllSessions();

        // 先过滤和分页，仅翻译当前页会话。
        List<UserSession> filteredList = allSessions.stream()
                .filter(session -> matchQuery(session, queryDTO))
                .collect(Collectors.toList());

        // 手动分页
        int total = filteredList.size();
        int current = pageQuery.getCurrent() != null && pageQuery.getCurrent() > 0
                ? pageQuery.getCurrent() : 1;
        int size = pageQuery.getSize() != null && pageQuery.getSize() > 0
                ? pageQuery.getSize() : 10;
        long offset = (long) (current - 1) * size;
        int fromIndex = (int) Math.min(Math.max(offset, 0L), total);
        int toIndex = (int) Math.min((long) fromIndex + size, total);

        List<UserSession> pageSessions = fromIndex < total
                ? filteredList.subList(fromIndex, toIndex)
                : List.of();
        Map<String, SysDictDataVO> deviceTypes = pageSessions.isEmpty() ? Map.of() : sysDictApi
                .getDictItemMaps(List.of(DictTypeConstants.SYS_DEVICE_TYPE))
                .getOrDefault(DictTypeConstants.SYS_DEVICE_TYPE, Map.of());
        List<SysSession> pageList = pageSessions.stream()
                .map(session -> convertToSysSession(session, deviceTypes))
                .collect(Collectors.toList());

        return PageResult.of(pageList, (long) total, current, size);
    }


    /**
     * 匹配查询条件
     */
    private boolean matchQuery(UserSession session, SysSessionQueryDTO queryDTO) {
        if (queryDTO == null) return true;

        // 用户名模糊匹配
        if (StringUtils.isNotBlank(queryDTO.getUserName())
                && (session.getUserName() == null || !session.getUserName().contains(queryDTO.getUserName()))) {
            return false;
        }
        // 昵称模糊匹配
        if (StringUtils.isNotBlank(queryDTO.getNickName())
                && (session.getNickName() == null || !session.getNickName().contains(queryDTO.getNickName()))) {
            return false;
        }
        // 登录IP模糊匹配
        if (StringUtils.isNotBlank(queryDTO.getLoginIp())
                && (session.getLoginIp() == null || !session.getLoginIp().contains(queryDTO.getLoginIp()))) {
            return false;
        }
        // 平台类型精确匹配
        if (queryDTO.getPlatformType() != null
                && (session.getPlatformType() == null || session.getPlatformType().getCode() != queryDTO.getPlatformType())) {
            return false;
        }
        return true;
    }

    /**
     * 踢出指定会话
     *
     * @param sessionId 会话ID
     * @return 操作是否成功，true表示踢出会话成功，false表示踢出会话失败
     */
    @Override
    public boolean kickOut(String sessionId) {
        return sessionHelper.kickOut(sessionId);
    }

    /**
     * 批量踢出多个会话
     *
     * @param sessionIds 会话ID列表
     * @return 成功踢出的会话数量
     */
    @Override
    public int kickOutBatch(List<String> sessionIds) {
        // 检查输入参数是否为空或空列表
        if (sessionIds == null || sessionIds.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (String sessionId : sessionIds) {
            if (sessionHelper.kickOut(sessionId)) {
                count++;
            }
        }
        return count;
    }


    /**
     * 将 UserSession 转换为 SysSession
     */
    private SysSession convertToSysSession(UserSession userSession,
                                           Map<String, SysDictDataVO> deviceTypes) {
        SysSession sysSession = new SysSession();
        sysSession.setSessionId(userSession.getSessionId());
        sysSession.setUserId(userSession.getUserId());
        sysSession.setUserName(userSession.getUserName());
        sysSession.setNickName(userSession.getNickName());
        sysSession.setPlatformType(userSession.getPlatformType());
        sysSession.setLoginIp(userSession.getLoginIp());
        sysSession.setIpRegion(userSession.getIpRegion());
        sysSession.setLoginTime(userSession.getLoginTime());
        sysSession.setLastAccessTime(userSession.getLastAccessTime());
        sysSession.setUserAgent(userSession.getUserAgent());
        sysSession.setDeviceType(userSession.getDeviceType());
        sysSession.setOs(userSession.getOs());
        sysSession.setBrowser(userSession.getBrowser());
        sysSession.setAccessExpireTime(userSession.getAccessExpireTime());
        sysSession.setRefreshExpireTime(userSession.getRefreshExpireTime());

        // 翻译设备类型
        if (userSession.getDeviceType() != null) {
            SysDictDataVO item = deviceTypes.get(userSession.getDeviceType());
            sysSession.setDeviceTypeLabel(item != null ? item.getDictLabel() : null);
        }

        return sysSession;
    }
}
