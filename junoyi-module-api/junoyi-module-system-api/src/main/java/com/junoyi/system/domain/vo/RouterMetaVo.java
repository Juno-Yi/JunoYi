package com.junoyi.system.domain.vo;

import lombok.Data;

/**
 * 路由元信息 VO
 *
 * @author Fan
 */
@Data
public class RouterMetaVo {

    /**
     * 菜单标题（支持 i18n key）
     */
    private String title;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 是否显示徽章
     */
    private Boolean showBadge;

    /**
     * 文本徽章内容
     */
    private String showTextBadge;

    /**
     * 是否隐藏菜单
     */
    private Boolean isHide;

    /**
     * 是否隐藏标签页
     */
    private Boolean isHideTab;

    /**
     * 外部链接地址
     */
    private String link;

    /**
     * 是否 iframe
     */
    private Boolean isIframe;

    /**
     * 是否缓存
     */
    private Boolean keepAlive;

    /**
     * 是否固定标签页
     */
    private Boolean fixedTab;

    /**
     * 激活菜单路径（详情页用）
     */
    private String activePath;

    /**
     * 是否全屏页面
     */
    private Boolean isFullPage;

    /**
     * 权限标识
     */
    private String authMark;
}
