package com.xsy.dailymate.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "todo")
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String title;
    private String content;
    private Integer priority;
    private Integer status;

    @Temporal(TemporalType.DATE)
    @Column(name = "date", nullable = false)
    private Date date;

    // 新增字段：软删除标记
    @Column(name = "is_delete")
    private Integer isDelete = 0; // 0 未删除，1 已删除

    // ================== 新增内容："创建/更新时间自动管理" ===================
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt; // 创建时间

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt; // 最后更新时间

    // ================== 新增字段："开始/结束/完成时间" ===================
    /**
     * 计划/实际开始时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time")
    private Date startTime;

    /**
     * 计划/实际截止时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time")
    private Date endTime;

    /**
     * 实际结束/完成时间
     * status==1 时建议赋值
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "finish_time")
    private Date finishTime;

    // ================== 新增字段：提醒功能 ===================
    /**
     * 是否启用提醒
     */
    @Column(name = "reminder_enabled")
    private Boolean reminderEnabled = false;

    /**
     * 提醒时间（提前多久提醒，单位：分钟）
     * 例如：0=准时提醒，15=提前 15 分钟，30=提前 30 分钟，60=提前 1 小时，1440=提前 1 天
     */
    @Column(name = "reminder_offset")
    private Integer reminderOffset = 0;

    /**
     * 实际提醒触发时间 = endTime - reminderOffset
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reminder_time")
    private Date reminderTime;

    /**
     * 是否已提醒
     */
    @Column(name = "is_reminded")
    private Boolean isReminded = false;

    // ================== 自动填充时间戳逻辑（无需手动赋值） ===================
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
