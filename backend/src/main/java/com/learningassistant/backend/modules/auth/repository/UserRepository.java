package com.learningassistant.backend.modules.auth.repository;

import com.learningassistant.backend.modules.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 用户数据访问
 * 模块: auth (成员1)
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    
    // 搜索用户（按用户名或昵称模糊匹配）
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.nickname LIKE %:keyword%")
    List<User> searchUsers(@Param("keyword") String keyword);
}
