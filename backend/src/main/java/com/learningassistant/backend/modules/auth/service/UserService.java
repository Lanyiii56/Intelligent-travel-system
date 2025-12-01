package com.learningassistant.backend.modules.auth.service;

import com.learningassistant.backend.modules.auth.model.User;
import com.learningassistant.backend.modules.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 * 模块: auth (成员1)
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User register(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User login(String username, String rawPassword) {
        User u = userRepository.findByUsername(username);
        if (u == null) return null;
        if (passwordEncoder.matches(rawPassword, u.getPassword())) return u;
        return null;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
