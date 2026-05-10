package com.xsy.dailymate.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

/**
 * 用户设置实体
 */
@Data
@Entity
@Table(name = "user_settings")
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    /**
     * 语言设置：zh-CN=简体中文，en-US=英语，ja-JP=日语，ko-KR=韩语
     */
    @Column(name = "language", length = 20)
    private String language = "zh-CN";

    /**
     * 主题风格：light=明亮，dark=黑暗，blue=蓝色，green=绿色，purple=紫色
     */
    @Column(name = "theme", length = 20)
    private String theme = "light";

    /**
     * 背景类型：color=纯色，image=图片
     */
    @Column(name = "background_type", length = 20)
    private String backgroundType = "color";

    /**
     * 背景色（HEX）
     */
    @Column(name = "background_color", length = 20)
    private String backgroundColor = "#f5f7fa";

    /**
     * 背景图 URL
     */
    @Lob
    @Column(name = "background_url", columnDefinition = "LONGTEXT")
    private String backgroundUrl;

    /**
     * 背景透明度（0-100）
     */
    @Column(name = "background_opacity")
    private Integer backgroundOpacity = 100;

    /**
     * 背景尺寸（cover/contain/100%/auto 100%）
     */
    @Column(name = "background_size", length = 20)
    private String backgroundSize = "cover";

    /**
     * 背景位置（center/left top/right top/...）
     */
    @Column(name = "background_position", length = 20)
    private String backgroundPosition = "center";

    /**
     * 是否启用通知
     */
    @Column(name = "notification_enabled")
    private Boolean notificationEnabled = true;

    /**
     * 是否启用声音提醒
     */
    @Column(name = "sound_enabled")
    private Boolean soundEnabled = false;

    /**
     * 是否启用紧凑模式
     */
    @Column(name = "compact_mode")
    private Boolean compactMode = false;

    /**
     * 是否启用动画效果
     */
    @Column(name = "animation_enabled")
    private Boolean animationEnabled = true;

    /**
     * 待办事项默认优先级：1=高，2=中，3=低
     */
    @Column(name = "default_priority")
    private Integer defaultPriority = 2;

    /**
     * 待办事项默认提醒时间（分钟）：0=无，15=提前 15 分钟
     */
    @Column(name = "default_reminder_offset")
    private Integer defaultReminderOffset = 0;

    /**
     * 首页默认视图：dashboard=仪表盘，todos=待办，bills=账单
     */
    @Column(name = "default_home_view", length = 20)
    private String defaultHomeView = "dashboard";

    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    /**
     * 更新时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}
