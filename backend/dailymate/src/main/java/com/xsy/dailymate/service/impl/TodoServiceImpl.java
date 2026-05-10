package com.xsy.dailymate.service.impl;

import com.xsy.dailymate.entity.Todo;
import com.xsy.dailymate.repository.TodoRepository;
import com.xsy.dailymate.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {
    @Autowired
    private TodoRepository todoRepository;

    @Override
    public List<Todo> getTodoListByUser(Long userId) {
        return todoRepository.findByUserIdAndIsDelete(userId, 0);
    }

    @Override
    public List<Todo> getTodoListByUserAndDate(Long userId, Date date) {
        return todoRepository.findByUserIdAndDateAndIsDelete(userId, date, 0);
    }

    @Override
    public Todo addTodo(Todo todo) {
        todo.setIsDelete(0);
        // 防呆设计：确保 date 字段不为 null
        if (todo.getDate() == null) {
            todo.setDate(new Date());
        }
        return todoRepository.save(todo);
    }

    @Override
    public void deleteTodo(Long id) {
        Todo todo = todoRepository.findById(id).orElse(null);
        if (todo != null && todo.getIsDelete() != null && todo.getIsDelete() == 0) {
            todo.setIsDelete(1);
            todoRepository.save(todo);
        }
        // todoRepository.deleteById(id); // 被软删除替代
    }

    @Override
    public Todo updateTodo(Todo todo) {
        // ========== 新增代码：任务完成时自动填充finishTime ==========
        if (todo.getStatus() != null && todo.getStatus() == 1) {
            // 未设置或finishTime为null时，填入当前时间
            if (todo.getFinishTime() == null) {
                todo.setFinishTime(new Date());
            }
        } else {
            // 非完成状态可以选择不填finishTime
            // todo.setFinishTime(null); // 如需要清空可打开
        }
        // ===========================
        return todoRepository.save(todo);
    }

    @Override
    public Todo findById(Long id) {
        Todo todo = todoRepository.findById(id).orElse(null);
        if (todo != null && todo.getIsDelete() != null && todo.getIsDelete() == 0) {
            return todo;
        }
        return null;
    }

    // ===== 新增功能实现 =====

    @Override
    public List<Todo> getTodoListByUserAndStatus(Long userId, Integer status) {
        return todoRepository.findByUserIdAndStatusAndIsDelete(userId, status, 0);
    }

    @Override
    public void batchUpdateStatus(List<Long> ids, Integer newStatus) {
        List<Todo> list = todoRepository.findAllById(ids);
        for (Todo todo : list) {
            if (todo.getIsDelete() != null && todo.getIsDelete() == 0) {
                todo.setStatus(newStatus);
                // ========== 新增代码：批量完成时自动填充finishTime ==========
                if (newStatus == 1) {
                    // 未设置或finishTime为null时，填入当前时间
                    if (todo.getFinishTime() == null) {
                        todo.setFinishTime(new Date());
                    }
                } else {
                    // 非完成批量处理可以选择不填finishTime
                    // todo.setFinishTime(null); // 如需要清空可打开
                }
                // ===========================
            }
        }
        todoRepository.saveAll(list);
    }

    @Override
    public void batchDelete(List<Long> ids) {
        List<Todo> list = todoRepository.findAllById(ids);
        for (Todo todo : list) {
            if (todo.getIsDelete() != null && todo.getIsDelete() == 0) {
                todo.setIsDelete(1);
            }
        }
        todoRepository.saveAll(list);
    }

    @Override
    public void hardDelete(Long id) {
        todoRepository.deleteById(id);
    }

    @Override
    public void batchHardDelete(List<Long> ids) {
        todoRepository.deleteAllById(ids);
    }

    @Override
    public void restore(Long id) {
        Todo todo = todoRepository.findById(id).orElse(null);
        if (todo != null && todo.getIsDelete() != null && todo.getIsDelete() == 1) {
            todo.setIsDelete(0);
            todoRepository.save(todo);
        }
    }

    @Override
    public List<Todo> searchTodo(Long userId, String keyword) {
        return todoRepository.findByUserIdAndIsDeleteAndTitleContainingOrContentContaining(
                userId, 0, keyword, keyword);
    }

    @Override
    public List<Todo> getTodoListByUserAndDateRange(Long userId, Date startDate, Date endDate) {
        return todoRepository.findByUserIdAndIsDeleteAndDateBetween(userId, 0, startDate, endDate);
    }

    @Override
    public void updatePriority(Long id, Integer priority) {
        Todo todo = todoRepository.findById(id).orElse(null);
        if (todo != null && todo.getIsDelete() != null && todo.getIsDelete() == 0) {
            todo.setPriority(priority);
            todoRepository.save(todo);
        }
    }

    @Override
    public long countUnfinished(Long userId) {
        return todoRepository.countByUserIdAndStatusAndIsDelete(userId, 0, 0);
    }

    @Override
    public List<Todo> getTodoListByUserAndPriority(Long userId, Integer priority) {
        return todoRepository.findByUserIdAndPriorityAndIsDelete(userId, priority, 0);
    }
}
