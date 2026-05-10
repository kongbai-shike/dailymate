package com.xsy.dailymate.dto.request;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * 更新用户信息请求 DTO
 */
@Data
public class UpdateUserProfileRequest {

    @Size(min = 3, max = 20, message = "用户名长度应为 3-20 个字符")
    private String username;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String avatar;
}
