package com.xsy.dailymate.service;

import com.xsy.dailymate.entity.Todo;
import java.util.Date;
import java.util.List;

public interface TodoService {
    List<Todo> getTodoListByUserAndDate(Long userId, Date date);
    List<Todo> getTodoListByUser(Long userId);
    Todo addTodo(Todo todo);
    void deleteTodo(Long id);
    Todo updateTodo(Todo todo);
    Todo findById(Long id);

    // 新增：筛选状态
    List<Todo> getTodoListByUserAndStatus(Long userId, Integer status);

    // 新增：批量设置状态
    void batchUpdateStatus(List<Long> ids, Integer newStatus);

    // 新增：批量软删除
    void batchDelete(List<Long> ids);

    // 新增：彻底物理删除
    void hardDelete(Long id);

    // 新增：批量彻底物理删除
    void batchHardDelete(List<Long> ids);

    // 新增：恢复已软删除
    void restore(Long id);

    // 新增：模糊查询
    List<Todo> searchTodo(Long userId, String keyword);

    // 新增：按时间区间查询
    List<Todo> getTodoListByUserAndDateRange(Long userId, Date startDate, Date endDate);

    // 新增：置顶与优先级变更
    void updatePriority(Long id, Integer priority);

    // 新增：统计未完成数
    long countUnfinished(Long userId);

    // 新增：按优先级筛选
    List<Todo> getTodoListByUserAndPriority(Long userId, Integer priority);
}
