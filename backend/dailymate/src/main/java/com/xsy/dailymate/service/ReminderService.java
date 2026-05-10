package com.xsy.dailymate.service;

import com.xsy.dailymate.entity.Todo;
import com.xsy.dailymate.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 待办提醒服务
 */
@Slf4j
@Service
public class ReminderService {

    @Autowired
    private TodoRepository todoRepository;

    // 存储所有活跃的 SSE 连接 (userId -> emitter)
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 注册 SSE 连接
     */
    public SseEmitter register(Long userId) {
        SseEmitter emitter = new SseEmitter(0L); // 永不过期
        emitters.put(userId, emitter);

        // 完成时移除
        emitter.onCompletion(() -> {
            emitters.remove(userId);
            log.info("用户 {} SSE 连接完成，当前连接数：{}", userId, emitters.size());
        });
        emitter.onTimeout(() -> {
            emitters.remove(userId);
            log.info("用户 {} SSE 连接超时，当前连接数：{}", userId, emitters.size());
        });
        emitter.onError((e) -> {
            emitters.remove(userId);
            log.info("用户 {} SSE 连接错误：{}, 当前连接数：{}", userId, e.getMessage(), emitters.size());
        });

        log.info("用户 {} 注册 SSE 连接，当前连接数：{}", userId, emitters.size());
        return emitter;
    }

    /**
     * 取消注册 SSE 连接
     */
    public void unregister(Long userId) {
        emitters.remove(userId);
        log.info("用户 {} 取消 SSE 连接，当前连接数：{}", userId, emitters.size());
    }

    /**
     * 发送提醒消息
     */
    public void sendReminder(Long userId, Todo todo) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                Map<String, Object> data = new HashMap<>();
                data.put("type", "reminder");
                data.put("todoId", todo.getId());
                data.put("title", todo.getTitle());
                data.put("endTime", todo.getEndTime());
                data.put("message", getReminderMessage(todo));
                emitter.send(data);
                log.info("已发送提醒给用户 {}，待办：{}", userId, todo.getTitle());
            } catch (Exception e) {
                log.error("发送提醒失败：{}", e.getMessage());
                emitters.remove(userId);
            }
        } else {
            log.warn("用户 {} 没有活跃的 SSE 连接，无法发送提醒", userId);
        }
    }

    /**
     * 生成提醒消息
     */
    private String getReminderMessage(Todo todo) {
        if (todo.getEndTime() == null) {
            return "待办事项即将到期：" + todo.getTitle();
        }

        long diff = todo.getEndTime().getTime() - System.currentTimeMillis();
        long minutes = diff / (1000 * 60);

        if (minutes <= 0) {
            return "待办事项已到期：" + todo.getTitle();
        } else if (minutes < 60) {
            return "待办事项将在 " + minutes + " 分钟后到期：" + todo.getTitle();
        } else {
            long hours = minutes / 60;
            return "待办事项将在 " + hours + " 小时 " + (minutes % 60) + " 分钟后到期：" + todo.getTitle();
        }
    }

    /**
     * 定时任务：每分钟检查一次需要提醒的待办事项
     */
    @Scheduled(cron = "0 * * * * *") // 每分钟执行一次
    public void checkReminders() {
        log.debug("开始检查待办提醒...");

        Date now = new Date();
        // 查找所有需要提醒但尚未提醒的待办事项
        List<Todo> todos = todoRepository.findByReminderEnabledTrueAndIsRemindedFalse();

        for (Todo todo : todos) {
            if (todo.getReminderTime() != null && todo.getReminderTime().before(now)) {
                // 需要发送提醒
                sendReminder(todo.getUserId(), todo);
                // 标记为已提醒
                todo.setIsReminded(true);
                todoRepository.save(todo);
                log.info("触发提醒：用户 {} 的待办事项 '{}'", todo.getUserId(), todo.getTitle());
            }
        }
    }

    /**
     * 计算提醒时间
     */
    public Date calculateReminderTime(Date endTime, Integer reminderOffset) {
        if (endTime == null || reminderOffset == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        calendar.add(Calendar.MINUTE, -reminderOffset);
        return calendar.getTime();
    }
}
