package com.xsy.dailymate.service.impl;

import com.xsy.dailymate.entity.User;
import com.xsy.dailymate.repository.UserRepository;
import com.xsy.dailymate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

//    @Override
//    public User findByUsername(String username) {
//        return userRepository.findByUsername(username, 0);
//    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsernameAndIsDelete(username, 0);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // 修改：只返回未删除用户
    @Override
    public List<User> findAll() {
        return userRepository.findByIsDelete(0);
    }

    @Override
    public User findById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        // 若用户未被软删除才返回
        if (user != null && user.getIsDelete() != null && user.getIsDelete() == 0) {
            return user;
        }
        return null;
    }

    // 软删除替换硬删除
    @Override
    public void deleteById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null && user.getIsDelete() != null && user.getIsDelete() == 0) {
            user.setIsDelete(1);
            userRepository.save(user);
        }
        // userRepository.deleteById(id); // 软删除，替换了物理删除
    }
}
