package com.learningassistant.backend.modules.auth.controller;

import com.learningassistant.backend.modules.auth.model.User;
import com.learningassistant.backend.modules.auth.service.UserService;
import com.learningassistant.backend.common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 模块: auth (成员1)
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            User user = new User();
            user.setUsername(req.getUsername());
            user.setPassword(req.getPassword());
            user.setNickname(req.getNickname() != null ? req.getNickname() : req.getUsername());
            user.setAge(req.getAge());
            user.setGender(req.getGender());
            
            User saved = userService.register(user);
            return ResponseEntity.ok(new RegisterResponse(saved.getId(), saved.getUsername(), saved.getNickname()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        User u = userService.login(req.getUsername(), req.getPassword());
        if (u == null) {
            return ResponseEntity.status(401).body("用户名或密码错误");
        }
        String token = JwtUtil.generateToken(u.getUsername());
        return ResponseEntity.ok(new LoginResponse(token, u));
    }

    // DTO 类
    public static class LoginRequest {
        private String username;
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String token;
        private UserInfo user;
        public LoginResponse(String token, User u) {
            this.token = token;
            this.user = new UserInfo(u.getId(), u.getUsername(), u.getNickname(), u.getAvatarUrl());
        }
        public String getToken() { return token; }
        public UserInfo getUser() { return user; }
    }

    public static class UserInfo {
        private Long id;
        private String username;
        private String nickname;
        private String avatarUrl;
        public UserInfo(Long id, String username, String nickname, String avatarUrl) {
            this.id = id;
            this.username = username;
            this.nickname = nickname;
            this.avatarUrl = avatarUrl;
        }
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getNickname() { return nickname; }
        public String getAvatarUrl() { return avatarUrl; }
    }

    public static class RegisterRequest {
        private String username;
        private String password;
        private String nickname;
        private Integer age;
        private String gender;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
    }

    public static class RegisterResponse {
        private Long id;
        private String username;
        private String nickname;
        public RegisterResponse(Long id, String username, String nickname) {
            this.id = id;
            this.username = username;
            this.nickname = nickname;
        }
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getNickname() { return nickname; }
    }
    
    // 更新用户资料
    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId, @RequestBody UpdateProfileRequest req) {
        try {
            User updated = userService.updateProfile(userId, req.getNickname(), req.getAge(), req.getGender(), req.getMotto());
            return ResponseEntity.ok(new UserInfo(updated.getId(), updated.getUsername(), updated.getNickname(), updated.getAvatarUrl()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 获取用户信息
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UserDetailInfo(user));
    }
    
    // 搜索用户
    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsers(@RequestParam String keyword) {
        java.util.List<User> users = userService.searchUsers(keyword);
        java.util.List<UserDetailInfo> result = users.stream()
            .map(UserDetailInfo::new)
            .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    // 更新头像
    @PutMapping("/avatar/{userId}")
    public ResponseEntity<?> updateAvatar(@PathVariable Long userId, @RequestBody UpdateAvatarRequest req) {
        try {
            User updated = userService.updateAvatar(userId, req.getAvatarUrl());
            return ResponseEntity.ok(new UserInfo(updated.getId(), updated.getUsername(), updated.getNickname(), updated.getAvatarUrl()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 更新资料请求 DTO
    public static class UpdateProfileRequest {
        private String nickname;
        private Integer age;
        private String gender;
        private String motto;
        
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getMotto() { return motto; }
        public void setMotto(String motto) { this.motto = motto; }
    }
    
    // 更新头像请求 DTO
    public static class UpdateAvatarRequest {
        private String avatarUrl;
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    }
    
    // 用户详细信息 DTO
    public static class UserDetailInfo {
        private Long id;
        private String username;
        private String nickname;
        private String avatarUrl;
        private Integer age;
        private String gender;
        private String createTime;
        
        public UserDetailInfo(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.nickname = user.getNickname();
            this.avatarUrl = user.getAvatarUrl();
            this.age = user.getAge();
            this.gender = user.getGender();
            this.createTime = user.getCreateTime() != null ? user.getCreateTime().toString() : null;
        }
        
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getNickname() { return nickname; }
        public String getAvatarUrl() { return avatarUrl; }
        public Integer getAge() { return age; }
        public String getGender() { return gender; }
        public String getCreateTime() { return createTime; }
    }
}
