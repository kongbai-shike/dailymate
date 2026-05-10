// UserService.java
package com.xsy.dailymate.service;

import com.xsy.dailymate.entity.User;
import java.util.List;

public interface UserService {
    User findByUsername(String username);
    User saveUser(User user);
    List<User> findAll();
    User findById(Long id);
    void deleteById(Long id);
}
