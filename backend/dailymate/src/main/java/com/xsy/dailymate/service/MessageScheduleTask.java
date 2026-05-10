package com.xsy.dailymate.service;

import com.xsy.dailymate.entity.Todo;
import com.xsy.dailymate.entity.User;
import com.xsy.dailymate.repository.TodoRepository;
import com.xsy.dailymate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * 消息定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageScheduleTask {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final MessageService messageService;

    /**
     * 每分钟检查一次 DDL 提醒
     * 提醒时间点：1 天、12 小时、6 小时、2 小时、1 小时、5 分钟
     */
    @Scheduled(cron = "0 * * * * ?")
    public void checkDdlReminder() {
        log.info("开始检查 DDL 提醒...");

        LocalDateTime now = LocalDateTime.now();
        List<Todo> allTodos = todoRepository.findAll();

        for (Todo todo : allTodos) {
            // 只检查未完成的待办事项
            if (todo.getStatus() != 0) {
                continue;
            }

            Date endTimeDate = todo.getEndTime();
            if (endTimeDate == null) {
                continue;
            }
            LocalDateTime endTime = endTimeDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

            Duration duration = Duration.between(now, endTime);
            long minutesUntilDdl = duration.toMinutes();

            // 只处理未来的时间
            if (minutesUntilDdl <= 0) {
                continue;
            }

            // 检查是否已经发送过该时间点的提醒
            String reminderKey = "ddl_reminder_" + todo.getId() + "_";

            // 1 天提醒 (1440 分钟)
            if (minutesUntilDdl >= 1439 && minutesUntilDdl <= 1441) {
                String key = reminderKey + "1d";
                if (!hasSentReminder(todo.getUserId(), key)) {
                    sendDdlReminder(todo, "1 天");
                    markReminderSent(todo.getUserId(), key);
                }
            }
            // 12 小时提醒 (720 分钟)
            else if (minutesUntilDdl >= 719 && minutesUntilDdl <= 721) {
                String key = reminderKey + "12h";
                if (!hasSentReminder(todo.getUserId(), key)) {
                    sendDdlReminder(todo, "12 小时");
                    markReminderSent(todo.getUserId(), key);
                }
            }
            // 6 小时提醒 (360 分钟)
            else if (minutesUntilDdl >= 359 && minutesUntilDdl <= 361) {
                String key = reminderKey + "6h";
                if (!hasSentReminder(todo.getUserId(), key)) {
                    sendDdlReminder(todo, "6 小时");
                    markReminderSent(todo.getUserId(), key);
                }
            }
            // 2 小时提醒 (120 分钟)
            else if (minutesUntilDdl >= 119 && minutesUntilDdl <= 121) {
                String key = reminderKey + "2h";
                if (!hasSentReminder(todo.getUserId(), key)) {
                    sendDdlReminder(todo, "2 小时");
                    markReminderSent(todo.getUserId(), key);
                }
            }
            // 1 小时提醒 (60 分钟)
            else if (minutesUntilDdl >= 59 && minutesUntilDdl <= 61) {
                String key = reminderKey + "1h";
                if (!hasSentReminder(todo.getUserId(), key)) {
                    sendDdlReminder(todo, "1 小时");
                    markReminderSent(todo.getUserId(), key);
                }
            }
            // 5 分钟提醒
            else if (minutesUntilDdl >= 4 && minutesUntilDdl <= 6) {
                String key = reminderKey + "5min";
                if (!hasSentReminder(todo.getUserId(), key)) {
                    sendDdlReminder(todo, "5 分钟");
                    markReminderSent(todo.getUserId(), key);
                }
            }
        }

        log.info("DDL 提醒检查完成");
    }

    /**
     * 发送 DDL 提醒
     */
    private void sendDdlReminder(Todo todo, String timeLeft) {
        User user = userRepository.findById(todo.getUserId()).orElse(null);
        if (user == null) {
            return;
        }

        String title = "⏰ DDL 提醒";
        String content = String.format(
            "您的待办事项【%s】还有 %s 就要截止了，请尽快处理！",
            todo.getTitle(),
            timeLeft
        );

        messageService.sendTodoReminder(todo.getUserId(), todo.getId(), title, content);
        log.info("发送 DDL 提醒：用户={}, 待办={}, 剩余时间={}", user.getUsername(), todo.getTitle(), timeLeft);
    }

    /**
     * 检查是否已发送过提醒（使用内存简单实现，生产环境建议使用 Redis）
     */
    private boolean hasSentReminder(Long userId, String key) {
        // 简单实现：检查消息表中是否已有相同内容的消息
        // 生产环境建议使用 Redis 存储
        return false; // 暂时总是发送
    }

    /**
     * 标记提醒已发送
     */
    private void markReminderSent(Long userId, String key) {
        // 简单实现，生产环境建议使用 Redis
        log.info("标记提醒已发送：userId={}, key={}", userId, key);
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void todoDailyReminder() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        // 查询今天到期的待办
        List<Todo> todos = todoRepository.findAllByEndTimeBetween(
            today.atStartOfDay(),
            tomorrow.atStartOfDay()
        );

        for (Todo todo : todos) {
            if (todo.getStatus() == 0) { // 只提醒未完成的
                User user = userRepository.findById(todo.getUserId()).orElse(null);
                if (user != null) {
                    messageService.sendTodoReminder(
                        todo.getUserId(),
                        todo.getId(),
                        "待办事项提醒",
                        "您有待办事项【" + todo.getTitle() + "】今天到期，请及时处理！"
                    );
                }
            }
        }
    }

    /**
     * 每周一早上 8 点发送周待办汇总
     */
    @Scheduled(cron = "0 0 8 ? * MON")
    public void todoWeeklySummary() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            LocalDate today = LocalDate.now();
            LocalDate nextWeek = today.plusWeeks(1);

            List<Todo> todos = todoRepository.findAllByUserIdAndEndTimeBetween(
                user.getId(),
                today.atStartOfDay(),
                nextWeek.atStartOfDay()
            );

            long undoneCount = todos.stream().filter(t -> t.getStatus() == 0).count();

            if (undoneCount > 0) {
                messageService.sendSystemNotification(
                    user.getId(),
                    "本周待办汇总",
                    "您本周还有 " + undoneCount + " 项待办事项未完成，加油！"
                );
            }
        }
    }

    /**
     * 每月 1 号早上 9 点发送月度账单汇总提醒
     */
    @Scheduled(cron = "0 0 9 1 * ?")
    public void billMonthlySummary() {
        // 这里可以添加月度账单汇总逻辑
        // 由于账单服务未注入，暂时跳过实现
    }
}
