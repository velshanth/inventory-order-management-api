package com.guvi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "orders")
public class OrderModel {
    @Id
    private String id;
    private String userId;
    private List<OrderItemModel> orderedItems;
    private Double totalCost;
    private OrderStatus status;
    private Instant orderedAt;

    public OrderModel() {

    }

    public OrderModel(String id, String userId, List<OrderItemModel> orderedItems, Double totalCost, OrderStatus status, Instant orderedAt) {
        this.id = id;
        this.userId = userId;
        this.orderedItems = orderedItems;
        this.totalCost = totalCost;
        this.status = status;
        this.orderedAt = orderedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<OrderItemModel> getOrderedItems() {
        return orderedItems;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Instant getOrderedAt() {
        return orderedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
