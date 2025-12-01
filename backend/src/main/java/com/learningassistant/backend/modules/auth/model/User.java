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
    private String avatarUrl;

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
}
