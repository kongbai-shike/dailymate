// 修改信息DTO
package com.xsy.dailymate.dto;

import lombok.Data;

@Data
public class UpdateUserProfileRequest {
    private String email;
    private String avatar;
    private String username; // 如允许昵称可修改
}
