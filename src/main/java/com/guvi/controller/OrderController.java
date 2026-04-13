package com.guvi.controller;

import com.guvi.dto.CreateOrderRequest;
import com.guvi.model.OrderModel;
import com.guvi.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderModel> getAll(){
        return orderService.getAll();
    }

    @GetMapping("/myorders")
    public ResponseEntity<?> getMyOrders() {
        List<OrderModel> orders = orderService.getMyOrders();
        if (orders.isEmpty()) {
            return ResponseEntity.ok("No orders found");
        }
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable String userId) {
        List<OrderModel> orders = orderService.getByUserId(userId);
        if (orders.isEmpty()) {
            return ResponseEntity.ok("No orders found for user: " + userId);
        }
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public OrderModel getOrder(@PathVariable String id){
        return orderService.getByID(id);
    }

    @PostMapping
    public ResponseEntity<OrderModel> create(@Valid @RequestBody CreateOrderRequest req){
        OrderModel created  = orderService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("cancel/{id}")
    public ResponseEntity<OrderModel> cancel(@PathVariable String id){
        OrderModel cancelled = orderService.cancelOrder(id);
        return ResponseEntity.status(HttpStatus.OK).body(cancelled);
    }

}
