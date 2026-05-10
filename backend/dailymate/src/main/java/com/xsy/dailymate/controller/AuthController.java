package com.xsy.dailymate.controller;

import com.xsy.dailymate.common.Result;
import com.xsy.dailymate.dto.request.LoginRequest;
import com.xsy.dailymate.dto.request.RegisterRequest;
import com.xsy.dailymate.entity.User;
import com.xsy.dailymate.service.UserService;
import com.xsy.dailymate.util.JwtUtil;
import com.xsy.dailymate.util.PasswordUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterRequest request) {
        // 检查用户名是否已存在
        User exist = userService.findByUsername(request.getUsername());
        if (exist != null) {
            return Result.error(400, "用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        user.setAvatar(request.getAvatar());
        user.setEmail(request.getEmail());

        User saved = userService.saveUser(user);
        return Result.success("注册成功", saved);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.findByUsername(request.getUsername());

        // 检查用户是否存在或已被删除
        if (user == null || (user.getIsDelete() != null && user.getIsDelete() == 1)) {
            return Result.error(401, "用户名或密码错误");
        }

        // 验证密码
        if (!PasswordUtil.match(request.getPassword(), user.getPassword())) {
            return Result.error(401, "用户名或密码错误");
        }

        // 生成 JWT Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        // 返回用户信息（不包含密码）
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setEmail(user.getEmail());
        safeUser.setAvatar(user.getAvatar());
        safeUser.setCreatedAt(user.getCreatedAt());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", safeUser);

        return Result.success("登录成功", data);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT 无状态，前端删除 token 即可
        return Result.successMessage("退出成功");
    }
}
