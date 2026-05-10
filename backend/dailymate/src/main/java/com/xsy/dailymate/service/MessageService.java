package com.xsy.dailymate.service;

import com.xsy.dailymate.dto.PageResult;
import com.xsy.dailymate.dto.request.MessageRequest;
import com.xsy.dailymate.entity.Message;

/**
 * 消息通知 Service 接口
 */
public interface MessageService {

    /**
     * 发送消息
     */
    Message sendMessage(MessageRequest request);

    /**
     * 发送系统通知
     */
    void sendSystemNotification(Long userId, String title, String content);

    /**
     * 发送待办提醒
     */
    void sendTodoReminder(Long userId, Long todoId, String title, String content);

    /**
     * 发送账单提醒
     */
    void sendBillReminder(Long userId, Long billId, String title, String content);

    /**
     * 获取用户消息列表
     */
    PageResult<Message> getUserMessages(Long userId, Integer page, Integer size);

    /**
     * 获取用户未读消息数量
     */
    long getUnreadCount(Long userId);

    /**
     * 标记消息为已读
     */
    void markAsRead(Long messageId);

    /**
     * 批量标记消息为已读
     */
    void markBatchAsRead(Long userId, Long[] messageIds);

    /**
     * 删除消息
     */
    void deleteMessage(Long messageId, Long userId);

    /**
     * 删除所有已读消息
     */
    void deleteAllRead(Long userId);
}
