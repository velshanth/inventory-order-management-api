package com.guvi.service;

import com.guvi.config.SecurityUtil;
import com.guvi.dto.CreateOrderRequest;
import com.guvi.dto.OrderItemRequest;
import com.guvi.error.BadRequestException;
import com.guvi.error.ResourceNotFoundException;
import com.guvi.model.*;
import com.guvi.repo.OrderRepository;
import com.guvi.repo.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderModel create(CreateOrderRequest req) {

        List<OrderItemRequest> orderedItemsDTO = req.getOrderedItems();

        if (orderedItemsDTO == null || orderedItemsDTO.isEmpty()) {
            throw new BadRequestException("Items cannot be empty");
        }

        List<OrderItemModel> finalOrderedItems = new ArrayList<>();
        Double totalCost = 0.0;

        for (OrderItemRequest item : orderedItemsDTO) {
            if (isBlank(item.getProductId())) {
                throw new BadRequestException("Product ID cannot be empty");
            }

            ProductModel product = getById(item.getProductId());
            if (product.getStatus() == null || product.getStatus() != ProductStatus.ACTIVE) {
                throw new BadRequestException("Product '" + product.getId() + "' is not active");
            }

            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new BadRequestException("Quantity must be greater than 0");
            }

            if (product.getPrice() == null || product.getPrice() < 0) {
                throw new BadRequestException("Invalid product price");
            }

            if (product.getStockQuantity() == null || product.getStockQuantity() < item.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getId());
            }

            OrderItemModel orderItem = new OrderItemModel(
                    product.getId(),
                    item.getQuantity(),
                    product.getName(),
                    product.getPrice()
            );

            finalOrderedItems.add(orderItem);
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
            totalCost += product.getPrice() * item.getQuantity();
        }

        String userID = SecurityUtil.getCurrentUserId();
        OrderModel order = new OrderModel(
                null,
                userID,
                finalOrderedItems,
                totalCost,
                OrderStatus.CONFIRMED,
                Instant.now()
        );
        return orderRepository.save(order);
    }

    public List<OrderModel> getAll(){
        return orderRepository.findAll();
    }

    public OrderModel getByID(String id){
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found for id: " + id));
    }

    public List<OrderModel> getMyOrders(){
        String userID = SecurityUtil.getCurrentUserId();
        return orderRepository.findByUserId(userID);
    }

    public List<OrderModel> getByUserId(String userId){
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public OrderModel cancelOrder(String id){
       OrderModel order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found for id: " + id));
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order already cancelled");
        }
        List<OrderItemModel> orderItems = order.getOrderedItems();
        if(order.getStatus() == OrderStatus.CONFIRMED) {
            for (OrderItemModel orderItem : orderItems) {
                Integer quantity = orderItem.getQuantity();
                ProductModel product = getById(orderItem.getProductId());
                product.setStockQuantity(product.getStockQuantity() + quantity);
                productRepository.save(product);
            }
        }
        else{
            throw new BadRequestException("Order cannot be cancelled in current state");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    public boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private ProductModel getById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for id: " + id));
    }
}