package com.xsy.dailymate.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;
    private String avatar;

    // 新增字段：软删除标记
    @Column(name = "is_delete")
    private Integer isDelete = 0; // 0未删除，1已删除

    // ================== 新增内容："创建/更新时间自动管理" ===================
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt; // 创建时间

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt; // 最后更新时间

    // 当实体新建时自动赋值
    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // 当实体被更新时自动赋值
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}
