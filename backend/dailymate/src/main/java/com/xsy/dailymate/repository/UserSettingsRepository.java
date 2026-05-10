package com.xsy.dailymate.repository;

import com.xsy.dailymate.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    /**
     * 根据用户 ID 查找设置
     */
    Optional<UserSettings> findByUserId(Long userId);

    /**
     * 删除用户设置
     */
    void deleteByUserId(Long userId);
}
