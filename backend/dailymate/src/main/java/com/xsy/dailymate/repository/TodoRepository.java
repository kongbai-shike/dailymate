package com.xsy.dailymate.repository;

import com.xsy.dailymate.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    // 基本功能
    List<Todo> findByUserIdAndIsDelete(Long userId, Integer isDelete);
    List<Todo> findByUserIdAndDateAndIsDelete(Long userId, Date date, Integer isDelete);

    // 新增功能支持
    List<Todo> findByUserIdAndStatusAndIsDelete(Long userId, Integer status, Integer isDelete);
    List<Todo> findByUserIdAndPriorityAndIsDelete(Long userId, Integer priority, Integer isDelete);
    long countByUserIdAndStatusAndIsDelete(Long userId, Integer status, Integer isDelete);

    // 日期区间
    List<Todo> findByUserIdAndIsDeleteAndDateBetween(Long userId, Integer isDelete, Date start, Date end);

    // 模糊搜索
    List<Todo> findByUserIdAndIsDeleteAndTitleContainingOrContentContaining(Long userId, Integer isDelete, String title, String content);

    // 按结束时间范围查询
    List<Todo> findAllByEndTimeBetween(LocalDateTime start, LocalDateTime end);

    // 按用户 ID 和结束时间范围查询
    List<Todo> findAllByUserIdAndEndTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);

    // ================== 提醒功能 ===================
    /**
     * 查找所有启用提醒但未提醒的待办事项
     */
    List<Todo> findByReminderEnabledTrueAndIsRemindedFalse();
}
