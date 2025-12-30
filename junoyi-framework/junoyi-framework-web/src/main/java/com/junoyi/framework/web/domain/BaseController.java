package com.junoyi.framework.web.domain;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageQuery;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.utils.ServletUtils;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Set;

/**
 * BaseController 类是所有控制器类的基类
 * 提供日志、用户信息、分页、请求响应等基础功能
 *
 * @author Fan
 */
public class BaseController {

    /**
     * 日志记录器实例
     */
    protected final JunoYiLog log = JunoYiLogFactory.getLogger(this.getClass());

    // ==================== 用户信息相关 ====================

    /**
     * 获取当前登录的用户
     */
    protected LoginUser getLoginUser() {
        return SecurityUtils.getLoginUser();
    }

    /**
     * 获取当前登录用户的用户名
     */
    protected String getUserName() {
        return SecurityUtils.getUserName();
    }

    /**
     * 获取当前登录用户的昵称
     */
    protected String getNickName() {
        return SecurityUtils.getNickName();
    }

    /**
     * 获取当前登录用户的ID
     */
    protected Long getUserId() {
        return SecurityUtils.getUserId();
    }

    /**
     * 获取当前用户的角色ID集合
     */
    protected Set<Long> getRoles() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getRoles() : null;
    }

    /**
     * 获取当前用户的部门ID集合
     */
    protected Set<Long> getDepts() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getDepts() : null;
    }

    /**
     * 获取当前用户的权限集合
     */
    protected Set<String> getPermissions() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getPermissions() : null;
    }

    /**
     * 判断当前用户是否为超级管理员
     */
    protected boolean isSuperAdmin() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null && loginUser.isSuperAdmin();
    }

    // ==================== 分页相关 ====================

    /**
     * 获取分页参数（从请求参数中）
     * 支持参数: pageNum, pageSize, orderBy, orderType
     */
    protected PageQuery getPageQuery() {
        PageQuery query = new PageQuery();
        query.setPageNum(ServletUtils.getParameterToInt("pageNum", 1));
        query.setPageSize(ServletUtils.getParameterToInt("pageSize", 10));
        query.setOrderBy(ServletUtils.getParameter("orderBy"));
        query.setOrderType(ServletUtils.getParameter("orderType", "asc"));
        
        // 限制每页最大数量，防止恶意请求
        if (query.getPageSize() > 100) {
            query.setPageSize(100);
        }
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        if (query.getPageNum() < 1) {
            query.setPageNum(1);
        }
        
        return query;
    }

    /**
     * 构建 MyBatis-Plus 分页对象
     */
    protected <T> Page<T> buildPage() {
        PageQuery query = getPageQuery();
        return new Page<>(query.getPageNum(), query.getPageSize());
    }

    /**
     * 构建 MyBatis-Plus 分页对象（指定类型）
     */
    protected <T> Page<T> buildPage(PageQuery query) {
        return new Page<>(query.getPageNum(), query.getPageSize());
    }

    /**
     * 将 MyBatis-Plus IPage 转换为 PageResult
     */
    protected <T> PageResult<T> toPageResult(IPage<T> page) {
        return PageResult.of(
                page.getRecords(),
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
    }

    /**
     * 将列表转换为 PageResult（手动分页）
     */
    protected <T> PageResult<T> toPageResult(List<T> list, Long total) {
        PageQuery query = getPageQuery();
        return PageResult.of(list, total, query);
    }

    // ==================== 请求响应相关 ====================

    /**
     * 获取当前请求对象
     */
    protected HttpServletRequest getRequest() {
        return ServletUtils.getRequest();
    }

    /**
     * 获取当前响应对象
     */
    protected HttpServletResponse getResponse() {
        return ServletUtils.getResponse();
    }

    /**
     * 获取客户端IP
     */
    protected String getClientIp() {
        return ServletUtils.getClientIp();
    }

    /**
     * 获取 User-Agent
     */
    protected String getUserAgent() {
        return ServletUtils.getUserAgent();
    }

    /**
     * 获取请求参数（String）
     */
    protected String getParameter(String name) {
        return ServletUtils.getParameter(name);
    }

    /**
     * 获取请求参数（String，带默认值）
     */
    protected String getParameter(String name, String defaultValue) {
        return ServletUtils.getParameter(name, defaultValue);
    }

    /**
     * 获取请求参数（Integer）
     */
    protected Integer getParameterToInt(String name) {
        return ServletUtils.getParameterToInt(name);
    }

    /**
     * 获取请求参数（Integer，带默认值）
     */
    protected Integer getParameterToInt(String name, Integer defaultValue) {
        return ServletUtils.getParameterToInt(name, defaultValue);
    }

    /**
     * 获取请求参数（Boolean）
     */
    protected Boolean getParameterToBool(String name) {
        return ServletUtils.getParameterToBool(name);
    }

    /**
     * 获取请求参数（Boolean，带默认值）
     */
    protected Boolean getParameterToBool(String name, Boolean defaultValue) {
        return ServletUtils.getParameterToBool(name, defaultValue);
    }

    /**
     * 获取请求头
     */
    protected String getHeader(String name) {
        HttpServletRequest request = getRequest();
        return request != null ? request.getHeader(name) : null;
    }

    // ==================== 工具方法 ====================

    /**
     * 判断字符串是否为空
     */
    protected boolean isEmpty(String str) {
        return StringUtils.isBlank(str);
    }

    /**
     * 判断字符串是否不为空
     */
    protected boolean isNotEmpty(String str) {
        return StringUtils.isNotBlank(str);
    }
}
