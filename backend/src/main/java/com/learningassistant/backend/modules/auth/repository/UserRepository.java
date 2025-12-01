package com.learningassistant.backend.modules.auth.repository;

import com.learningassistant.backend.modules.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户数据访问
 * 模块: auth (成员1)
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
