package com.xsy.dailymate.service;

import com.xsy.dailymate.dto.request.UserSettingsRequest;
import com.xsy.dailymate.entity.UserSettings;
import com.xsy.dailymate.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 用户设置服务
 */
@Service
public class UserSettingsService {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    /**
     * 获取用户设置
     */
    public UserSettings getSettingsByUserId(Long userId) {
        Optional<UserSettings> settings = userSettingsRepository.findByUserId(userId);
        return settings.orElseGet(() -> createDefaultSettings(userId));
    }

    /**
     * 更新用户设置
     */
    @Transactional
    public UserSettings updateSettings(Long userId, UserSettingsRequest request) {
        UserSettings settings = getSettingsByUserId(userId);

        if (request.getLanguage() != null) {
            settings.setLanguage(request.getLanguage());
        }
        if (request.getTheme() != null) {
            settings.setTheme(request.getTheme());
        }
        if (request.getBackgroundType() != null) {
            settings.setBackgroundType(request.getBackgroundType());
        }
        if (request.getBackgroundColor() != null) {
            settings.setBackgroundColor(request.getBackgroundColor());
        }
        if (request.getBackgroundUrl() != null) {
            settings.setBackgroundUrl(request.getBackgroundUrl());
        }
        if (request.getBackgroundOpacity() != null) {
            settings.setBackgroundOpacity(request.getBackgroundOpacity());
        }
        if (request.getBackgroundSize() != null) {
            settings.setBackgroundSize(request.getBackgroundSize());
        }
        if (request.getBackgroundPosition() != null) {
            settings.setBackgroundPosition(request.getBackgroundPosition());
        }
        if (request.getNotificationEnabled() != null) {
            settings.setNotificationEnabled(request.getNotificationEnabled());
        }
        if (request.getSoundEnabled() != null) {
            settings.setSoundEnabled(request.getSoundEnabled());
        }
        if (request.getCompactMode() != null) {
            settings.setCompactMode(request.getCompactMode());
        }
        if (request.getAnimationEnabled() != null) {
            settings.setAnimationEnabled(request.getAnimationEnabled());
        }
        if (request.getDefaultPriority() != null) {
            settings.setDefaultPriority(request.getDefaultPriority());
        }
        if (request.getDefaultReminderOffset() != null) {
            settings.setDefaultReminderOffset(request.getDefaultReminderOffset());
        }
        if (request.getDefaultHomeView() != null) {
            settings.setDefaultHomeView(request.getDefaultHomeView());
        }

        return userSettingsRepository.save(settings);
    }

    /**
     * 创建默认设置
     */
    private UserSettings createDefaultSettings(Long userId) {
        UserSettings settings = new UserSettings();
        settings.setUserId(userId);
        settings.setLanguage("zh-CN");
        settings.setTheme("light");
        settings.setBackgroundType("color");
        settings.setBackgroundColor("#f5f7fa");
        settings.setBackgroundOpacity(100);
        settings.setBackgroundSize("cover");
        settings.setBackgroundPosition("center");
        settings.setNotificationEnabled(true);
        settings.setSoundEnabled(false);
        settings.setCompactMode(false);
        settings.setAnimationEnabled(true);
        settings.setDefaultPriority(2);
        settings.setDefaultReminderOffset(0);
        settings.setDefaultHomeView("dashboard");
        return userSettingsRepository.save(settings);
    }

    /**
     * 删除用户设置
     */
    @Transactional
    public void deleteSettings(Long userId) {
        userSettingsRepository.deleteByUserId(userId);
    }
}
