package com.learningassistant.backend.modules.auth.service;

import com.learningassistant.backend.modules.auth.model.User;
import com.learningassistant.backend.modules.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
    
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    /**
     * 更新用户资料
     */
    public User updateProfile(Long userId, String nickname, Integer age, String gender, String motto) {
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        User user = optUser.get();
        if (nickname != null && !nickname.isEmpty()) {
            user.setNickname(nickname);
        }
        if (age != null) {
            user.setAge(age);
        }
        if (gender != null) {
            user.setGender(gender);
        }
        return userRepository.save(user);
    }
    
    /**
     * 更新用户头像
     */
    public User updateAvatar(Long userId, String avatarUrl) {
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        User user = optUser.get();
        user.setAvatarUrl(avatarUrl);
        return userRepository.save(user);
    }
}
