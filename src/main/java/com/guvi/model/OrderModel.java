package com.guvi.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "orders")
public class OrderModel {

    @Id
    private String id;

    @NotBlank(message = "User ID is required")
    private String userId;

    @Valid
    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemModel> orderedItems;

    @NotNull(message = "Total cost is required")
    @PositiveOrZero(message = "Total cost cannot be negative")
    private Double totalCost;

    @NotNull(message = "Order status is required")
    private OrderStatus status;

    @NotNull(message = "Order date is required")
    private Instant orderedAt;

    public OrderModel() {
    }

    public OrderModel(String id, String userId,
                      List<OrderItemModel> orderedItems,
                      Double totalCost,
                      OrderStatus status,
                      Instant orderedAt) {
        this.id = id;
        this.userId = userId;
        this.orderedItems = orderedItems;
        this.totalCost = totalCost;
        this.status = status;
        this.orderedAt = orderedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setOrderedItems(List<OrderItemModel> orderedItems) {
        this.orderedItems = orderedItems;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
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

    public void setOrderedAt(Instant orderedAt) {
        this.orderedAt = orderedAt;
    }
}