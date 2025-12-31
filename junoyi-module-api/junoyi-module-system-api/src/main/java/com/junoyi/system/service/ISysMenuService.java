package com.junoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.domain.dto.SysMenuDTO;
import com.junoyi.system.domain.dto.SysMenuQueryDTO;
import com.junoyi.system.domain.dto.SysMenuSortItem;
import com.junoyi.system.domain.po.SysMenu;
import com.junoyi.system.domain.vo.SysMenuVO;

import java.util.List;

/**
 * 系统菜单业务接口
 *
 * @author Fan
 */
public interface ISysMenuService {

    /**
     * 查询菜单列表（树形结构）
     *
     * @param queryDTO 查询参数
     * @return 菜单树形列表
     */
    List<SysMenuVO> getMenuTree(SysMenuQueryDTO queryDTO);

    /**
     * 查询菜单列表（分页）
     *
     * @param queryDTO 查询参数
     * @param page 分页对象
     * @return 分页结果
     */
    PageResult<SysMenuVO> getMenuPage(SysMenuQueryDTO queryDTO, Page<SysMenu> page);

    /**
     * 查询菜单列表（不分页）
     *
     * @param queryDTO 查询参数
     * @return 菜单列表
     */
    List<SysMenuVO> getMenuList(SysMenuQueryDTO queryDTO);

    /**
     * 根据ID查询菜单详情
     *
     * @param id 菜单ID
     * @return 菜单详情
     */
    SysMenuVO getMenuById(Long id);

    /**
     * 新增菜单
     *
     * @param menuDTO 菜单数据
     * @return 新增的菜单ID
     */
    Long addMenu(SysMenuDTO menuDTO);

    /**
     * 更新菜单
     *
     * @param menuDTO 菜单数据
     * @return 是否成功
     */
    boolean updateMenu(SysMenuDTO menuDTO);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 是否成功
     */
    boolean deleteMenu(Long id);

    /**
     * 批量删除菜单
     *
     * @param ids 菜单ID列表
     * @return 是否成功
     */
    boolean deleteMenuBatch(List<Long> ids);

    /**
     * 批量更新菜单排序
     *
     * @param sortList 排序列表
     * @return 是否成功
     */
    boolean updateMenuSort(List<SysMenuSortItem> sortList);
}
