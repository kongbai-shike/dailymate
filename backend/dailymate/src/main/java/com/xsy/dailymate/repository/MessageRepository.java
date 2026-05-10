package com.xsy.dailymate.repository;

import com.xsy.dailymate.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 消息通知 Repository
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * 分页查询用户消息
     */
    Page<Message> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 查询用户未读消息数量
     */
    long countByUserIdAndIsRead(Long userId, Integer isRead);

    /**
     * 查询用户未读消息
     */
    Page<Message> findByUserIdAndIsReadOrderByCreatedAtDesc(Long userId, Integer isRead, Pageable pageable);
}
