package com.xsy.dailymate.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 待办事项请求 DTO
 */
@Data
public class TodoRequest {

    private Long id;

    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过 100 个字符")
    private String title;

    @Size(max = 500, message = "内容长度不能超过 500 个字符")
    private String content;

    @NotNull(message = "优先级不能为空")
    private Integer priority;

    @NotNull(message = "状态不能为空")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private Date date;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date finishTime;

    // ================== 提醒功能字段 ===================
    /**
     * 是否启用提醒
     */
    private Boolean reminderEnabled = false;

    /**
     * 提醒提前时间（单位：分钟）
     * 0=准时提醒，15=提前 15 分钟，30=提前 30 分钟，60=提前 1 小时，1440=提前 1 天
     */
    private Integer reminderOffset = 0;
}
