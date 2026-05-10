package com.xsy.dailymate.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息通知实体类
 */
@Data
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 接收用户 ID
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 消息标题
     */
    @Column(length = 200)
    private String title;

    /**
     * 消息内容
     */
    @Column(length = 1000)
    private String content;

    /**
     * 消息类型：1-系统通知 2-待办提醒 3-账单提醒 4-其他
     */
    @Column(nullable = false)
    private Integer type = 1;

    /**
     * 是否已读：0-未读 1-已读
     */
    @Column(nullable = false)
    private Integer isRead = 0;

    /**
     * 关联业务 ID（如待办 ID、账单 ID）
     */
    private Long relatedId;

    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 读取时间
     */
    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
