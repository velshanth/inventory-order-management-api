package com.guvi.dto;

import com.guvi.model.OrderItemModel;
import com.guvi.model.OrderStatus;
import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.List;

public class CreateOrderRequest {
    @NotEmpty
    private List<OrderItemRequest> orderedItems;

    public CreateOrderRequest() {
    }
    public List<OrderItemRequest> getOrderedItems() {
        return orderedItems;
    }

    public void setOrderedItems(List<OrderItemRequest> orderedItems) {
        this.orderedItems = orderedItems;
    }
}
