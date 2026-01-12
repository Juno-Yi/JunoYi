package com.junoyi.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.exception.MenuHasChildrenException;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.system.convert.SysMenuConverter;
import com.junoyi.system.domain.dto.SysMenuDTO;
import com.junoyi.system.domain.dto.SysMenuQueryDTO;
import com.junoyi.system.domain.bo.SysMenuSortItem;
import com.junoyi.system.domain.po.SysMenu;
import com.junoyi.system.domain.vo.SysMenuVO;
import com.junoyi.system.enums.SysMenuStatus;
import com.junoyi.system.mapper.SysMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 系统菜单业务实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements ISysMenuService {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(this.getClass());

    private final SysMenuMapper sysMenuMapper;
    private final SysMenuConverter sysMenuConverter;

    /**
     * 获取菜单树形结构
     *
     * @param queryDTO 查询条件DTO
     * @return 菜单树形结构VO列表
     */
    @Override
    public List<SysMenuVO> getMenuTree(SysMenuQueryDTO queryDTO) {
        log.debug("查询菜单树形列表, queryDTO: {}", queryDTO);
        // 查询所有菜单
        List<SysMenu> menus = queryMenuList(queryDTO);
        // 转换为 VO
        List<SysMenuVO> menuVOList = sysMenuConverter.toVoList(menus);
        // 构建树形结构
        return buildTree(menuVOList, 0L);
    }

    /**
     * 分页获取菜单列表
     *
     * @param queryDTO 查询条件DTO
     * @param page 分页对象
     * @return 分页结果
     */
    @Override
    public PageResult<SysMenuVO> getMenuPage(SysMenuQueryDTO queryDTO, Page<SysMenu> page) {
        log.debug("分页查询菜单列表, queryDTO: {}, page: {}/{}", queryDTO, page.getCurrent(), page.getSize());

        LambdaQueryWrapper<SysMenu> wrapper = buildQueryWrapper(queryDTO);
        Page<SysMenu> result = sysMenuMapper.selectPage(page, wrapper);

        List<SysMenuVO> voList = sysMenuConverter.toVoList(result.getRecords());
        return PageResult.of(voList, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    /**
     * 获取菜单列表
     *
     * @param queryDTO 查询条件DTO
     * @return 菜单VO列表
     */
    @Override
    public List<SysMenuVO> getMenuList(SysMenuQueryDTO queryDTO) {
        log.debug("查询菜单列表, queryDTO: {}", queryDTO);
        List<SysMenu> menus = queryMenuList(queryDTO);
        return sysMenuConverter.toVoList(menus);
    }

    /**
     * 根据ID获取菜单
     *
     * @param id 菜单ID
     * @return 菜单VO
     */
    @Override
    public SysMenuVO getMenuById(Long id) {
        SysMenu menu = sysMenuMapper.selectById(id);
        return menu != null ? sysMenuConverter.toVo(menu) : null;
    }

    /**
     * 添加菜单
     *
     * @param menuDTO 菜单DTO
     * @return 菜单ID
     */
    @Override
    public Long addMenu(SysMenuDTO menuDTO) {
        SysMenu menu = sysMenuConverter.toEntity(menuDTO);
        // 设置默认值
        if (menu.getParentId() == null)
            menu.setParentId(0L);
        if (menu.getSort() == null)
            menu.setSort(0);
        if (menu.getStatus() == null)
            menu.setStatus(SysMenuStatus.ENABLE.getCode());
        menu.setCreateTime(DateUtils.getNowDate());
        sysMenuMapper.insert(menu);
        return menu.getId();
    }

    /**
     * 更新菜单
     *
     * @param menuDTO 菜单DTO
     * @return 更新结果
     */
    @Override
    public boolean updateMenu(SysMenuDTO menuDTO) {
        if (menuDTO.getId() == null)
            return false;
        SysMenu menu = sysMenuConverter.toEntity(menuDTO);
        menu.setUpdateTime(DateUtils.getNowDate());
        return sysMenuMapper.updateById(menu) > 0;
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 删除结果
     */
    @Override
    public boolean deleteMenu(Long id) {
        // 检查是否有子菜单
        Long childCount = sysMenuMapper.selectCount(
                new LambdaQueryWrapper<SysMenu>()
                        .eq(SysMenu::getParentId, id)
        );
        if (childCount > 0)
            throw new MenuHasChildrenException("存在子菜单，无法删除");
        return sysMenuMapper.deleteById(id) > 0;
    }

    /**
     * 批量删除菜单
     *
     * @param ids 菜单ID列表
     * @return 删除结果
     */
    @Override
    public boolean deleteMenuBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty())
            return false;
        // 检查是否有子菜单
        Long childCount = sysMenuMapper.selectCount(
                new LambdaQueryWrapper<SysMenu>()
                        .in(SysMenu::getParentId, ids)
        );
        if (childCount > 0)
            throw new MenuHasChildrenException("存在子菜单，无法删除");
        return sysMenuMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 查询菜单列表
     *
     * @param queryDTO 查询条件DTO
     * @return 菜单实体列表
     */
    private List<SysMenu> queryMenuList(SysMenuQueryDTO queryDTO) {
        LambdaQueryWrapper<SysMenu> wrapper = buildQueryWrapper(queryDTO);
        return sysMenuMapper.selectList(wrapper);
    }

    /**
     * 构建查询条件
     *
     * @param queryDTO 查询条件DTO
     * @return Lambda查询包装器
     */
    private LambdaQueryWrapper<SysMenu> buildQueryWrapper(SysMenuQueryDTO queryDTO) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO != null) {
            wrapper.like(StringUtils.isNotBlank(queryDTO.getTitle()), SysMenu::getTitle, queryDTO.getTitle())
                    .eq(queryDTO.getMenuType() != null, SysMenu::getMenuType, queryDTO.getMenuType())
                    .eq(queryDTO.getStatus() != null, SysMenu::getStatus, queryDTO.getStatus());
        }

        wrapper.orderByAsc(SysMenu::getSort);

        return wrapper;
    }

    /**
     * 构建树形结构
     *
     * @param menus 菜单VO列表
     * @param parentId 父级ID
     * @return 树形结构的菜单VO列表
     */
    private List<SysMenuVO> buildTree(List<SysMenuVO> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> Objects.equals(menu.getParentId(), parentId))
                .peek(menu -> {
                    List<SysMenuVO> children = buildTree(menus, menu.getId());
                    if (!children.isEmpty()) {
                        menu.setChildren(children);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 批量更新菜单排序
     *
     * @param sortList 排序列表
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMenuSort(List<SysMenuSortItem> sortList) {
        if (sortList == null || sortList.isEmpty()) {
            return false;
        }
        log.debug("批量更新菜单排序, 数量: {}", sortList.size());
        
        for (SysMenuSortItem item : sortList) {
            if (item.getId() == null) {
                continue;
            }
            // 使用 LambdaUpdateWrapper 只更新指定字段，避免覆盖其他字段
            LambdaUpdateWrapper<SysMenu> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SysMenu::getId, item.getId())
                    .set(SysMenu::getParentId, item.getParentId())
                    .set(item.getSort() != null, SysMenu::getSort, item.getSort())
                    .set(item.getPath() != null, SysMenu::getPath, item.getPath())
                    .set(item.getComponent() != null, SysMenu::getComponent, item.getComponent())
                    .set(SysMenu::getUpdateTime, DateUtils.getNowDate())
                    .set(SysMenu::getUpdateBy, SecurityUtils.getUserName());
            sysMenuMapper.update(null, updateWrapper);
        }
        return true;
    }
}
