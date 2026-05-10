package com.xsy.dailymate.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.Date;

/**
 * 账单请求 DTO
 */
@Data
public class BillRequest {

    private Long id;

    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    @NotNull(message = "类型不能为空")
    private Integer type;

    @NotBlank(message = "分类不能为空")
    private String category;

    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须大于 0")
    private Double amount;

    @Size(max = 200, message = "备注长度不能超过 200 个字符")
    private String remark;

    @NotNull(message = "日期不能为空")
    private Date date;
}
