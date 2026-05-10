package com.xsy.dailymate.controller;

import com.xsy.dailymate.common.Result;
import com.xsy.dailymate.dto.request.UserSettingsRequest;
import com.xsy.dailymate.entity.UserSettings;
import com.xsy.dailymate.service.UserSettingsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户设置控制器
 */
@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    @Autowired
    private UserSettingsService userSettingsService;

    /**
     * 获取用户设置
     */
    @GetMapping
    public Result<UserSettings> getSettings(@RequestParam Long userId) {
        UserSettings settings = userSettingsService.getSettingsByUserId(userId);
        return Result.success(settings);
    }

    /**
     * 更新用户设置
     */
    @PutMapping
    public Result<UserSettings> updateSettings(@RequestParam Long userId,
                                                @Valid @RequestBody UserSettingsRequest request) {
        UserSettings settings = userSettingsService.updateSettings(userId, request);
        return Result.success("设置已保存", settings);
    }

    /**
     * 更新单个设置项
     */
    @PutMapping("/{key}")
    public Result<UserSettings> updateSetting(@RequestParam Long userId,
                                               @PathVariable String key,
                                               @RequestBody Object value) {
        UserSettingsRequest request = new UserSettingsRequest();

        switch (key) {
            case "language":
                request.setLanguage((String) value);
                break;
            case "theme":
                request.setTheme((String) value);
                break;
            case "backgroundType":
                request.setBackgroundType((String) value);
                break;
            case "backgroundColor":
                request.setBackgroundColor((String) value);
                break;
            case "backgroundUrl":
                request.setBackgroundUrl((String) value);
                break;
            case "backgroundOpacity":
                request.setBackgroundOpacity((Integer) value);
                break;
            case "backgroundSize":
                request.setBackgroundSize((String) value);
                break;
            case "backgroundPosition":
                request.setBackgroundPosition((String) value);
                break;
            case "notificationEnabled":
                request.setNotificationEnabled((Boolean) value);
                break;
            case "soundEnabled":
                request.setSoundEnabled((Boolean) value);
                break;
            case "compactMode":
                request.setCompactMode((Boolean) value);
                break;
            case "animationEnabled":
                request.setAnimationEnabled((Boolean) value);
                break;
            case "defaultPriority":
                request.setDefaultPriority((Integer) value);
                break;
            case "defaultReminderOffset":
                request.setDefaultReminderOffset((Integer) value);
                break;
            case "defaultHomeView":
                request.setDefaultHomeView((String) value);
                break;
            default:
                return Result.error(400, "未知的设置项：" + key);
        }

        UserSettings settings = userSettingsService.updateSettings(userId, request);
        return Result.success("设置已保存", settings);
    }

    /**
     * 重置为默认设置
     */
    @PostMapping("/reset")
    public Result<UserSettings> resetSettings(@RequestParam Long userId) {
        userSettingsService.deleteSettings(userId);
        UserSettings settings = userSettingsService.getSettingsByUserId(userId);
        return Result.success("已重置为默认设置", settings);
    }
}
