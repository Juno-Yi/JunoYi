package com.junoyi.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.system.domain.po.SysMenu;
import com.junoyi.system.domain.vo.RouterItemVO;
import com.junoyi.system.domain.vo.RouterMetaVO;
import com.junoyi.system.enums.SysMenuStatus;
import com.junoyi.system.enums.SysMenuType;
import com.junoyi.system.mapper.SysMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统路由服务实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysRouterServiceImpl implements ISysRouterService {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysRouterServiceImpl.class);

    private final SysMenuMapper sysMenuMapper;

    @Override
    public List<RouterItemVO> getUserRouter(LoginUser loginUser) {
        log.debug("[路由加载] 开始加载用户路由, userId={}", loginUser.getUserId());
        
        // 查询所有启用的菜单（目录+菜单）
        List<SysMenu> allMenus = sysMenuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>()
                        .eq(SysMenu::getStatus, SysMenuStatus.ENABLE.getCode())
                        .in(SysMenu::getMenuType, SysMenuType.DIRECTORY.getCode(),SysMenuType.MENU.getCode())
                        .orderByAsc(SysMenu::getSort)
        );
        
        log.debug("[路由加载] 查询到菜单总数: {}", allMenus.size());
        
        List<SysMenu> userMenus;
        
        // 超级管理员获取所有菜单
        if (loginUser.isSuperAdmin()) {
            log.debug("[路由加载] 超级管理员, 返回所有菜单");
            userMenus = allMenus;
        } else {
            // 普通用户根据权限过滤菜单
            // 菜单的 permission 字段为空表示公开菜单，所有人可见
            // 菜单的 permission 字段不为空时，需要用户拥有该权限
            Set<String> userPermissions = loginUser.getPermissions();
            if (userPermissions == null) {
                userPermissions = new HashSet<>();
            }
            
            final Set<String> permissions = userPermissions;
            userMenus = allMenus.stream()
                    .filter(menu -> {
                        String perm = menu.getPermission();
                        // 无权限标识的菜单对所有登录用户可见
                        if (perm == null || perm.isBlank()) {
                            return true;
                        }
                        // 有权限标识的菜单需要用户拥有该权限
                        return permissions.contains(perm);
                    })
                    .collect(Collectors.toList());
            
            log.debug("[路由加载] 用户权限过滤后菜单数量: {}", userMenus.size());
        }
        
        // 构建树形结构
        List<RouterItemVO> routes = buildRouterTree(userMenus, 0L);

        log.debug("[路由加载] 完成, 顶级路由数量: {}", routes.size());
        return routes;
    }

    /**
     * 构建路由树
     * @param menus 菜单列表
     * @param parentId 父级ID
     * @return 路由列表
     */
    private List<RouterItemVO> buildRouterTree(List<SysMenu> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> Objects.equals(menu.getParentId(), parentId))
                .map(menu -> {
                    RouterItemVO item = new RouterItemVO();
                    item.setId(menu.getId());
                    item.setName(menu.getName());
                    item.setPath(menu.getPath());
                    item.setComponent(menu.getComponent());
                    item.setMeta(buildMeta(menu));
                    
                    // 递归构建子路由
                    List<RouterItemVO> children = buildRouterTree(menus, menu.getId());
                    if (!children.isEmpty()) {
                        item.setChildren(children);
                    }
                    
                    return item;
                })
                // 过滤：目录类型(menuType=0)且没有子菜单的不返回
                .filter(item -> {
                    // 查找对应的菜单获取类型
                    SysMenu menu = menus.stream()
                            .filter(m -> Objects.equals(m.getId(), item.getId()))
                            .findFirst()
                            .orElse(null);
                    if (menu == null) {
                        return true;
                    }
                    // 如果是目录类型且没有子菜单，则过滤掉
                    if (menu.getMenuType() == 0 && (item.getChildren() == null || item.getChildren().isEmpty())) {
                        log.debug("[路由加载] 过滤空目录: {}", menu.getTitle());
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * 构建路由元信息
     */
    private RouterMetaVO buildMeta(SysMenu menu) {
        RouterMetaVO meta = new RouterMetaVO();
        meta.setTitle(menu.getTitle());
        meta.setIcon(menu.getIcon());
        meta.setShowBadge(menu.getShowBadge() != null && menu.getShowBadge() == 1);
        meta.setShowTextBadge(menu.getShowTextBadge());
        meta.setIsHide(menu.getIsHide() != null && menu.getIsHide() == 1);
        meta.setIsHideTab(menu.getIsHideTab() != null && menu.getIsHideTab() == 1);
        meta.setLink(menu.getLink());
        meta.setIsIframe(menu.getIsIframe() != null && menu.getIsIframe() == 1);
        meta.setKeepAlive(menu.getKeepAlive() != null && menu.getKeepAlive() == 1);
        meta.setFixedTab(menu.getFixedTab() != null && menu.getFixedTab() == 1);
        meta.setActivePath(menu.getActivePath());
        meta.setIsFullPage(menu.getIsFullPage() != null && menu.getIsFullPage() == 1);
        meta.setAuthMark(menu.getPermission());
        return meta;
    }
}
