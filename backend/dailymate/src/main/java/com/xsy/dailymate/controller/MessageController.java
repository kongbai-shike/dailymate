package com.xsy.dailymate.controller;

import com.xsy.dailymate.common.Result;
import com.xsy.dailymate.dto.PageResult;
import com.xsy.dailymate.dto.request.MessageRequest;
import com.xsy.dailymate.entity.Message;
import com.xsy.dailymate.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息通知 Controller
 */
@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 发送消息
     */
    @PostMapping("/send")
    public Result<Message> sendMessage(@Valid @RequestBody MessageRequest request) {
        Message message = messageService.sendMessage(request);
        return Result.success(message);
    }

    /**
     * 获取消息列表
     */
    @GetMapping("/list")
    public Result<PageResult<Message>> getMessages(
        @RequestParam Long userId,
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "20") Integer size
    ) {
        PageResult<Message> result = messageService.getUserMessages(userId, page, size);
        return Result.success(result);
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread-count")
    public Result<Map<String, Object>> getUnreadCount(@RequestParam Long userId) {
        long count = messageService.getUnreadCount(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("count", count);
        return Result.success(data);
    }

    /**
     * 标记消息为已读
     */
    @PostMapping("/mark-read/{messageId}")
    public Result<Void> markAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
        return Result.success(null);
    }

    /**
     * 批量标记已读
     */
    @PostMapping("/mark-batch-read")
    public Result<Void> markBatchAsRead(
        @RequestParam Long userId,
        @RequestBody Long[] messageIds
    ) {
        messageService.markBatchAsRead(userId, messageIds);
        return Result.success(null);
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    public Result<Void> deleteMessage(
        @PathVariable Long messageId,
        @RequestParam Long userId
    ) {
        messageService.deleteMessage(messageId, userId);
        return Result.success(null);
    }

    /**
     * 删除所有已读消息
     */
    @DeleteMapping("/all-read")
    public Result<Void> deleteAllRead(@RequestParam Long userId) {
        messageService.deleteAllRead(userId);
        return Result.success(null);
    }
}
