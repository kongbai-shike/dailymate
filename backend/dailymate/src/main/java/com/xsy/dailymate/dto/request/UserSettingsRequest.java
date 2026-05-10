package com.xsy.dailymate.dto.request;

import lombok.Data;

/**
 * 用户设置请求 DTO
 */
@Data
public class UserSettingsRequest {

    /**
     * 语言设置：zh-CN=简体中文，en-US=英语，ja-JP=日语，ko-KR=韩语
     */
    private String language;

    /**
     * 主题风格：light=明亮，dark=黑暗，blue=蓝色，green=绿色，purple=紫色
     */
    private String theme;

    /**
     * 背景类型：color=纯色，image=图片
     */
    private String backgroundType;

    /**
     * 背景色（HEX）
     */
    private String backgroundColor;

    /**
     * 背景图 URL
     */
    private String backgroundUrl;

    /**
     * 背景透明度（0-100）
     */
    private Integer backgroundOpacity;

    /**
     * 背景尺寸（cover/contain/100%/auto 100%）
     */
    private String backgroundSize;

    /**
     * 背景位置（center/left top/right top/...）
     */
    private String backgroundPosition;

    /**
     * 是否启用通知
     */
    private Boolean notificationEnabled;

    /**
     * 是否启用声音提醒
     */
    private Boolean soundEnabled;

    /**
     * 是否启用紧凑模式
     */
    private Boolean compactMode;

    /**
     * 是否启用动画效果
     */
    private Boolean animationEnabled;

    /**
     * 待办事项默认优先级：1=高，2=中，3=低
     */
    private Integer defaultPriority;

    /**
     * 待办事项默认提醒时间（分钟）
     */
    private Integer defaultReminderOffset;

    /**
     * 首页默认视图：dashboard=仪表盘，todos=待办，bills=账单
     */
    private String defaultHomeView;
}
