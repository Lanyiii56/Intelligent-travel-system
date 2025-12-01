package com.learningassistant.backend.modules.order.controller;

import com.learningassistant.backend.modules.order.model.OrderEntity;
import com.learningassistant.backend.modules.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 * 模块: order (成员4)
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public OrderEntity create(@RequestParam Long userId, @RequestParam Long spotId, @RequestParam Double amount) {
        return orderService.createOrder(userId, spotId, amount);
    }

    @PostMapping("/pay/{orderId}")
    public OrderEntity pay(@PathVariable Long orderId) {
        return orderService.payOrder(orderId);
    }

    @GetMapping("/list/{userId}")
    public List<OrderEntity> list(@PathVariable Long userId) {
        return orderService.listByUser(userId);
    }
}
