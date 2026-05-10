package com.xsy.dailymate.repository;

import com.xsy.dailymate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
//    User findByUsername(String username, Integer isDelete);
    // 新增：查未删除用户
    List<User> findByIsDelete(Integer isDelete);
    User findByUsernameAndIsDelete(String username, Integer isDelete);
}
