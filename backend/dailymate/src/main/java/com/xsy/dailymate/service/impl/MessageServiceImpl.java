package com.xsy.dailymate.service.impl;

import com.xsy.dailymate.dto.PageResult;
import com.xsy.dailymate.dto.request.MessageRequest;
import com.xsy.dailymate.entity.Message;
import com.xsy.dailymate.repository.MessageRepository;
import com.xsy.dailymate.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 消息通知 Service 实现
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    public Message sendMessage(MessageRequest request) {
        Message message = new Message();
        message.setUserId(request.getUserId());
        message.setTitle(request.getTitle());
        message.setContent(request.getContent());
        message.setType(request.getType());
        message.setRelatedId(request.getRelatedId());
        return messageRepository.save(message);
    }

    @Override
    public void sendSystemNotification(Long userId, String title, String content) {
        MessageRequest request = new MessageRequest();
        request.setUserId(userId);
        request.setTitle(title);
        request.setContent(content);
        request.setType(1);
        sendMessage(request);
    }

    @Override
    public void sendTodoReminder(Long userId, Long todoId, String title, String content) {
        MessageRequest request = new MessageRequest();
        request.setUserId(userId);
        request.setTitle(title);
        request.setContent(content);
        request.setType(2);
        request.setRelatedId(todoId);
        sendMessage(request);
    }

    @Override
    public void sendBillReminder(Long userId, Long billId, String title, String content) {
        MessageRequest request = new MessageRequest();
        request.setUserId(userId);
        request.setTitle(title);
        request.setContent(content);
        request.setType(3);
        request.setRelatedId(billId);
        sendMessage(request);
    }

    @Override
    public PageResult<Message> getUserMessages(Long userId, Integer page, Integer size) {
        Page<Message> messagePage = messageRepository.findByUserIdOrderByCreatedAtDesc(
            userId,
            PageRequest.of(page, size, Sort.by("createdAt").descending())
        );
        return PageResult.of(messagePage.getContent(), messagePage.getTotalElements());
    }

    @Override
    public long getUnreadCount(Long userId) {
        return messageRepository.countByUserIdAndIsRead(userId, 0);
    }

    @Override
    @Transactional
    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("消息不存在"));
        message.setIsRead(1);
        message.setReadAt(LocalDateTime.now());
        messageRepository.save(message);
    }

    @Override
    @Transactional
    public void markBatchAsRead(Long userId, Long[] messageIds) {
        Arrays.stream(messageIds)
            .forEach(id -> {
                Message message = messageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("消息不存在"));
                if (message.getUserId().equals(userId)) {
                    message.setIsRead(1);
                    message.setReadAt(LocalDateTime.now());
                    messageRepository.save(message);
                }
            });
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("消息不存在"));
        if (!message.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该消息");
        }
        messageRepository.delete(message);
    }

    @Override
    @Transactional
    public void deleteAllRead(Long userId) {
        Page<Message> readMessages = messageRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(
            userId, 1, PageRequest.of(0, 1000)
        );
        messageRepository.deleteAll(readMessages.getContent());
    }
}
