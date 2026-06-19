package com.guvi.service;

import com.guvi.config.SecurityUtil;
import com.guvi.dto.CreateOrderRequest;
import com.guvi.dto.OrderItemRequest;
import com.guvi.error.BadRequestException;
import com.guvi.error.ResourceNotFoundException;
import com.guvi.model.*;
import com.guvi.repo.OrderRepository;
import com.guvi.repo.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void create_ShouldCreateOrderSuccessfully() {

        ProductModel product = new ProductModel();
        product.setId("P1");
        product.setName("Laptop");
        product.setPrice(50000.0);
        product.setStockQuantity(10.0);
        product.setStatus(ProductStatus.ACTIVE);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId("P1");
        itemRequest.setQuantity(2);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderedItems(List.of(itemRequest));

        when(productRepository.findById("P1"))
                .thenReturn(Optional.of(product));

        when(productRepository.save(any(ProductModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderRepository.save(any(OrderModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<SecurityUtil> securityUtil =
                     mockStatic(SecurityUtil.class)) {

            securityUtil.when(SecurityUtil::getCurrentUserId)
                    .thenReturn("USER1");

            OrderModel result = orderService.create(request);

            assertNotNull(result);
            assertEquals("USER1", result.getUserId());
            assertEquals(OrderStatus.CONFIRMED, result.getStatus());
            assertEquals(100000.0, result.getTotalCost());
            assertEquals(1, result.getOrderedItems().size());

            assertEquals(8, product.getStockQuantity());
        }
    }

    @Test
    void create_ShouldThrow_WhenItemsEmpty() {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderedItems(List.of());

        assertThrows(
                BadRequestException.class,
                () -> orderService.create(request)
        );
    }

    @Test
    void create_ShouldThrow_WhenProductIdBlank() {

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(" ");
        item.setQuantity(1);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderedItems(List.of(item));

        assertThrows(
                BadRequestException.class,
                () -> orderService.create(request)
        );
    }

    @Test
    void create_ShouldThrow_WhenProductNotFound() {

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId("P1");
        item.setQuantity(1);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderedItems(List.of(item));

        when(productRepository.findById("P1"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.create(request)
        );
    }

    @Test
    void create_ShouldThrow_WhenProductInactive() {

        ProductModel product = new ProductModel();
        product.setId("P1");
        product.setStatus(ProductStatus.INACTIVE);

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId("P1");
        item.setQuantity(1);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderedItems(List.of(item));

        when(productRepository.findById("P1"))
                .thenReturn(Optional.of(product));

        assertThrows(
                BadRequestException.class,
                () -> orderService.create(request)
        );
    }

    @Test
    void create_ShouldThrow_WhenQuantityInvalid() {

        ProductModel product = new ProductModel();
        product.setId("P1");
        product.setStatus(ProductStatus.ACTIVE);
        product.setPrice(100.0);
        product.setStockQuantity(10.0);

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId("P1");
        item.setQuantity(0);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderedItems(List.of(item));

        when(productRepository.findById("P1"))
                .thenReturn(Optional.of(product));

        assertThrows(
                BadRequestException.class,
                () -> orderService.create(request)
        );
    }

    @Test
    void create_ShouldThrow_WhenInsufficientStock() {

        ProductModel product = new ProductModel();
        product.setId("P1");
        product.setStatus(ProductStatus.ACTIVE);
        product.setPrice(100.0);
        product.setStockQuantity(1.0);

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId("P1");
        item.setQuantity(5);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderedItems(List.of(item));

        when(productRepository.findById("P1"))
                .thenReturn(Optional.of(product));

        assertThrows(
                BadRequestException.class,
                () -> orderService.create(request)
        );
    }

    @Test
    void getAll_ShouldReturnOrders() {

        List<OrderModel> orders = List.of(new OrderModel());

        when(orderRepository.findAll()).thenReturn(orders);

        List<OrderModel> result = orderService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void getById_ShouldReturnOrder() {

        OrderModel order = new OrderModel();
        order.setId("O1");

        when(orderRepository.findById("O1"))
                .thenReturn(Optional.of(order));

        OrderModel result = orderService.getByID("O1");

        assertEquals("O1", result.getId());
    }

    @Test
    void getById_ShouldThrowNotFound() {

        when(orderRepository.findById("O1"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getByID("O1")
        );
    }

    @Test
    void getMyOrders_ShouldReturnCurrentUserOrders() {

        List<OrderModel> orders = List.of(new OrderModel());

        when(orderRepository.findByUserId("USER1"))
                .thenReturn(orders);

        try (MockedStatic<SecurityUtil> securityUtil =
                     mockStatic(SecurityUtil.class)) {

            securityUtil.when(SecurityUtil::getCurrentUserId)
                    .thenReturn("USER1");

            List<OrderModel> result = orderService.getMyOrders();

            assertEquals(1, result.size());
        }
    }

    @Test
    void getByUserId_ShouldReturnOrders() {

        List<OrderModel> orders = List.of(new OrderModel());

        when(orderRepository.findByUserId("USER1"))
                .thenReturn(orders);

        List<OrderModel> result = orderService.getByUserId("USER1");

        assertEquals(1, result.size());
    }

    @Test
    void cancelOrder_ShouldCancelOrderAndRestoreStock() {

        ProductModel product = new ProductModel();
        product.setId("P1");
        product.setStockQuantity(5.0);

        OrderItemModel item = new OrderItemModel(
                "P1",
                2,
                "Laptop",
                50000.0
        );

        OrderModel order = new OrderModel(
                "O1",
                "USER1",
                List.of(item),
                100000.0,
                OrderStatus.CONFIRMED,
                Instant.now()
        );

        when(orderRepository.findById("O1"))
                .thenReturn(Optional.of(order));

        when(productRepository.findById("P1"))
                .thenReturn(Optional.of(product));

        when(orderRepository.save(any(OrderModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderModel result = orderService.cancelOrder("O1");

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        assertEquals(7, product.getStockQuantity());

        verify(productRepository).save(product);
    }

    @Test
    void cancelOrder_ShouldThrow_WhenAlreadyCancelled() {

        OrderModel order = new OrderModel();
        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findById("O1"))
                .thenReturn(Optional.of(order));

        assertThrows(
                BadRequestException.class,
                () -> orderService.cancelOrder("O1")
        );
    }

    @Test
    void cancelOrder_ShouldThrow_WhenOrderNotFound() {

        when(orderRepository.findById("O1"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.cancelOrder("O1")
        );
    }
}