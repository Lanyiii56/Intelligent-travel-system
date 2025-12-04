package com.learningassistant.backend.modules.order.controller;

import com.learningassistant.backend.modules.order.model.OrderEntity;
import com.learningassistant.backend.modules.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    public OrderEntity create(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long spotId = Long.valueOf(params.get("spotId").toString());
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        LocalDate visitDate = params.get("visitDate") != null ? 
            LocalDate.parse(params.get("visitDate").toString()) : null;
        Integer visitorCount = params.get("visitorCount") != null ? 
            Integer.valueOf(params.get("visitorCount").toString()) : 1;
        String contactName = params.get("contactName") != null ? 
            params.get("contactName").toString() : null;
        String contactPhone = params.get("contactPhone") != null ? 
            params.get("contactPhone").toString() : null;
        return orderService.createOrder(userId, spotId, amount, visitDate, visitorCount, contactName, contactPhone);
    }

    @PostMapping("/pay/{orderId}")
    public OrderEntity pay(@PathVariable Long orderId) {
        return orderService.payOrder(orderId);
    }
    
    @PostMapping("/cancel/{orderId}")
    public OrderEntity cancel(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }
    
    @GetMapping("/{orderId}")
    public OrderEntity get(@PathVariable Long orderId) {
        return orderService.getById(orderId);
    }

    @GetMapping("/list/{userId}")
    public List<OrderEntity> list(@PathVariable Long userId) {
        return orderService.listByUser(userId);
    }
}
