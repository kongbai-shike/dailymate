package com.xsy.dailymate.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 消息发送请求 DTO
 */
@Data
public class MessageRequest {

    /**
     * 接收用户 ID
     */
    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    /**
     * 消息标题
     */
    @NotBlank(message = "消息标题不能为空")
    private String title;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 消息类型：1-系统通知 2-待办提醒 3-账单提醒 4-其他
     */
    private Integer type = 1;

    /**
     * 关联业务 ID
     */
    private Long relatedId;
}
