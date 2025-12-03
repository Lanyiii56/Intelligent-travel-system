package com.learningassistant.backend.modules.auth.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体
 * 模块: auth (成员1)
 */
@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    private String nickname;
    private Integer age;
    private String gender;
    
    @Column(name = "avatar_url")
    private String avatarUrl;
    
    // 用户座右铭
    private String motto;
    
    // 用户积分
    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer points = 0;
    
    // 用户等级
    @Column(length = 50)
    private String level = "萌新旅者";

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
}
