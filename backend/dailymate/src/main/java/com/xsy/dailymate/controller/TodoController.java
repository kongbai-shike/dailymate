package com.xsy.dailymate.controller;

import com.xsy.dailymate.common.Result;
import com.xsy.dailymate.dto.request.TodoRequest;
import com.xsy.dailymate.entity.Todo;
import com.xsy.dailymate.service.ReminderService;
import com.xsy.dailymate.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 待办事项控制器
 */
@RestController
@RequestMapping("/api/todo")
@Validated
public class TodoController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private ReminderService reminderService;

    /**
     * 获取待办事项列表
     */
    @GetMapping("/list")
    public Result<List<Todo>> getTodoList(@RequestParam Long userId,
                                          @RequestParam(required = false)
                                          @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<Todo> todos;
        if (date != null) {
            todos = todoService.getTodoListByUserAndDate(userId, date);
        } else {
            todos = todoService.getTodoListByUser(userId);
        }
        return Result.success(todos);
    }

    /**
     * 获取待办事项详情
     */
    @GetMapping("/{id}")
    public Result<Todo> findById(@PathVariable Long id) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            return Result.error(404, "待办事项不存在");
        }
        return Result.success(todo);
    }

    /**
     * 添加待办事项
     */
    @PostMapping("/add")
    public Result<Todo> addTodo(@Valid @RequestBody TodoRequest request) {
        System.out.println("=== 接收到添加待办请求 ===");
        System.out.println("request.getDate(): " + request.getDate());
        System.out.println("request.getTitle(): " + request.getTitle());

        Todo todo = new Todo();
        todo.setUserId(request.getUserId());
        todo.setTitle(request.getTitle());
        todo.setContent(request.getContent());
        todo.setPriority(request.getPriority());
        todo.setStatus(request.getStatus());

        // 防呆设计：确保 date 字段不为 null
        if (request.getDate() != null) {
            todo.setDate(request.getDate());
            System.out.println("使用 request 的 date: " + todo.getDate());
        } else {
            // 如果前端没有传 date，使用当前日期
            todo.setDate(new java.util.Date());
            System.out.println("使用当前日期作为默认值：" + todo.getDate());
        }

        todo.setStartTime(request.getStartTime());
        todo.setEndTime(request.getEndTime());
        todo.setFinishTime(request.getFinishTime());

        // 设置提醒相关字段
        todo.setReminderEnabled(request.getReminderEnabled() != null ? request.getReminderEnabled() : false);
        todo.setReminderOffset(request.getReminderOffset() != null ? request.getReminderOffset() : 0);
        // 计算提醒时间
        if (request.getReminderEnabled() && request.getEndTime() != null) {
            todo.setReminderTime(reminderService.calculateReminderTime(
                request.getEndTime(),
                request.getReminderOffset()
            ));
        }
        todo.setIsReminded(false);

        System.out.println("=== 准备保存 ===");
        System.out.println("todo.getDate(): " + todo.getDate());

        Todo saved = todoService.addTodo(todo);
        return Result.success("添加成功", saved);
    }

    /**
     * 更新待办事项
     */
    @PutMapping("/update")
    public Result<Todo> updateTodo(@Valid @RequestBody TodoRequest request) {
        Todo existing = todoService.findById(request.getId());
        if (existing == null) {
            return Result.error(404, "待办事项不存在");
        }

        existing.setTitle(request.getTitle());
        existing.setContent(request.getContent());
        existing.setPriority(request.getPriority());
        existing.setStatus(request.getStatus());
        existing.setStartTime(request.getStartTime());
        existing.setEndTime(request.getEndTime());
        existing.setFinishTime(request.getFinishTime());

        // 更新提醒相关字段
        if (request.getReminderEnabled() != null) {
            existing.setReminderEnabled(request.getReminderEnabled());
        }
        if (request.getReminderOffset() != null) {
            existing.setReminderOffset(request.getReminderOffset());
        }
        // 重新计算提醒时间
        if (request.getReminderEnabled() && request.getEndTime() != null) {
            existing.setReminderTime(reminderService.calculateReminderTime(
                request.getEndTime(),
                request.getReminderOffset()
            ));
            existing.setIsReminded(false); // 重置提醒状态
        } else {
            existing.setReminderTime(null);
        }

        Todo updated = todoService.updateTodo(existing);
        return Result.success("更新成功", updated);
    }

    /**
     * 删除待办事项（软删除）
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return Result.successMessage("删除成功");
    }

    /**
     * 按状态获取待办事项
     */
    @GetMapping("/by-status")
    public Result<List<Todo>> getTodosByStatus(@RequestParam Long userId,
                                                @RequestParam Integer status) {
        List<Todo> todos = todoService.getTodoListByUserAndStatus(userId, status);
        return Result.success(todos);
    }

    /**
     * 批量更新状态
     */
    @PutMapping("/batch/finish")
    public Result<Void> batchUpdateStatus(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");
        Integer status = (Integer) request.get("status");

        if (ids == null || ids.isEmpty()) {
            return Result.error(400, "请选择要操作的项目");
        }

        todoService.batchUpdateStatus(ids.stream().map(Long::valueOf).toList(), status);
        return Result.successMessage("操作成功");
    }

    /**
     * 批量软删除
     */
    @PutMapping("/batch/delete")
    public Result<Void> batchDelete(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");

        if (ids == null || ids.isEmpty()) {
            return Result.error(400, "请选择要删除的项目");
        }

        todoService.batchDelete(ids.stream().map(Long::valueOf).toList());
        return Result.successMessage("批量删除成功");
    }

    /**
     * 彻底物理删除
     */
    @DeleteMapping("/hard-delete/{id}")
    public Result<Void> hardDelete(@PathVariable Long id) {
        todoService.hardDelete(id);
        return Result.successMessage("彻底删除成功");
    }

    /**
     * 批量彻底物理删除
     */
    @PutMapping("/batch/hard-delete")
    public Result<Void> batchHardDelete(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");

        if (ids == null || ids.isEmpty()) {
            return Result.error(400, "请选择要删除的项目");
        }

        todoService.batchHardDelete(ids.stream().map(Long::valueOf).toList());
        return Result.successMessage("批量彻底删除成功");
    }

    /**
     * 恢复已软删除的待办
     */
    @PutMapping("/restore/{id}")
    public Result<Void> restore(@PathVariable Long id) {
        todoService.restore(id);
        return Result.successMessage("恢复成功");
    }

    /**
     * 模糊查找（按标题或内容）
     */
    @GetMapping("/search")
    public Result<List<Todo>> searchTodo(@RequestParam Long userId,
                                          @RequestParam String keyword) {
        List<Todo> todos = todoService.searchTodo(userId, keyword);
        return Result.success(todos);
    }

    /**
     * 日期区间查询
     */
    @GetMapping("/range")
    public Result<List<Todo>> getTodosInRange(@RequestParam Long userId,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        List<Todo> todos = todoService.getTodoListByUserAndDateRange(userId, start, end);
        return Result.success(todos);
    }

    /**
     * 修改优先级
     */
    @PutMapping("/priority/{id}")
    public Result<Void> updatePriority(@PathVariable Long id,
                                        @RequestBody Map<String, Integer> request) {
        Integer priority = request.get("priority");
        if (priority == null) {
            return Result.error(400, "优先级不能为空");
        }
        todoService.updatePriority(id, priority);
        return Result.successMessage("更新成功");
    }

    /**
     * 获取未完成数量
     */
    @GetMapping("/unfinished-count")
    public Result<Long> getUnfinishedCount(@RequestParam Long userId) {
        long count = todoService.countUnfinished(userId);
        return Result.success(count);
    }

    /**
     * 按优先级筛选
     */
    @GetMapping("/by-priority")
    public Result<List<Todo>> getByPriority(@RequestParam Long userId,
                                             @RequestParam Integer priority) {
        List<Todo> todos = todoService.getTodoListByUserAndPriority(userId, priority);
        return Result.success(todos);
    }

    // ================== 提醒功能 API ===================

    /**
     * 注册 SSE 消息推送连接
     */
    @GetMapping(value = "/remind/stream/{userId}", produces = "text/event-stream")
    public SseEmitter streamReminders(@PathVariable Long userId) {
        return reminderService.register(userId);
    }

    /**
     * 设置待办提醒
     */
    @PutMapping("/remind/set/{id}")
    public Result<Void> setReminder(@PathVariable Long id,
                                     @RequestBody Map<String, Object> request) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            return Result.error(404, "待办事项不存在");
        }

        Boolean enabled = (Boolean) request.get("enabled");
        Integer offset = (Integer) request.get("offset");

        if (enabled == null) {
            return Result.error(400, "enabled 参数不能为空");
        }

        todo.setReminderEnabled(enabled);
        todo.setReminderOffset(offset != null ? offset : 0);

        if (enabled && todo.getEndTime() != null) {
            todo.setReminderTime(reminderService.calculateReminderTime(
                todo.getEndTime(),
                todo.getReminderOffset()
            ));
            todo.setIsReminded(false);
        } else {
            todo.setReminderTime(null);
        }

        todoService.updateTodo(todo);
        return Result.successMessage("提醒设置成功");
    }

    /**
     * 取消待办提醒
     */
    @PutMapping("/remind/cancel/{id}")
    public Result<Void> cancelReminder(@PathVariable Long id) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            return Result.error(404, "待办事项不存在");
        }

        todo.setReminderEnabled(false);
        todo.setReminderTime(null);
        todo.setIsReminded(false);

        todoService.updateTodo(todo);
        return Result.successMessage("取消提醒成功");
    }

    /**
     * 标记提醒为已读
     */
    @PutMapping("/remind/ack/{id}")
    public Result<Void> acknowledgeReminder(@PathVariable Long id) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            return Result.error(404, "待办事项不存在");
        }

        todo.setIsReminded(true);
        todoService.updateTodo(todo);
        return Result.successMessage("操作成功");
    }
}
