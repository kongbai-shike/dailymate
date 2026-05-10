package com.xsy.dailymate.controller;

import com.xsy.dailymate.common.Result;
import com.xsy.dailymate.dto.request.ChangePasswordRequest;
import com.xsy.dailymate.dto.request.UpdateUserProfileRequest;
import com.xsy.dailymate.entity.User;
import com.xsy.dailymate.service.UserService;
import com.xsy.dailymate.util.PasswordUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result<User> getCurrentUser(@RequestParam Long userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 返回安全用户信息（不包含密码）
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setEmail(user.getEmail());
        safeUser.setAvatar(user.getAvatar());
        safeUser.setCreatedAt(user.getCreatedAt());

        return Result.success(safeUser);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<User> updateUser(@Valid @RequestBody UpdateUserProfileRequest request,
                                    @RequestParam Long userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 检查新用户名是否已被其他用户使用
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            User exist = userService.findByUsername(request.getUsername());
            if (exist != null && !exist.getId().equals(userId)) {
                return Result.error(400, "用户名已存在");
            }
        }

        // 更新用户信息
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }

        User updated = userService.saveUser(user);
        return Result.success("更新成功", updated);
    }

    /**
     * 修改密码
     */
    @PutMapping("/change-password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                        @RequestParam Long userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 验证旧密码
        if (!PasswordUtil.match(request.getOldPassword(), user.getPassword())) {
            return Result.error(400, "当前密码错误");
        }

        // 更新密码
        user.setPassword(PasswordUtil.encode(request.getNewPassword()));
        userService.saveUser(user);

        return Result.successMessage("密码修改成功");
    }
}
