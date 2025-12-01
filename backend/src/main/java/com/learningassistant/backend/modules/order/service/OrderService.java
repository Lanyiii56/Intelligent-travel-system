package com.learningassistant.backend.modules.order.service;

import com.learningassistant.backend.modules.order.model.OrderEntity;
import com.learningassistant.backend.modules.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务
 * 模块: order (成员4)
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public OrderEntity createOrder(Long userId, Long spotId, Double amount) {
        OrderEntity order = new OrderEntity();
        order.setUserId(userId);
        order.setSpotId(spotId);
        order.setAmount(amount);
        order.setOrderStatus("pending");
        return orderRepository.save(order);
    }

    public OrderEntity payOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return null;
        order.setOrderStatus("paid");
        order.setPayTime(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public List<OrderEntity> listByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
