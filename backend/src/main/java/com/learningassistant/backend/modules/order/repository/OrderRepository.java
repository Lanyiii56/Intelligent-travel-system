package com.learningassistant.backend.modules.order.repository;

import com.learningassistant.backend.modules.order.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 订单数据访问
 * 模块: order (成员4)
 */
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUserId(Long userId);
}
