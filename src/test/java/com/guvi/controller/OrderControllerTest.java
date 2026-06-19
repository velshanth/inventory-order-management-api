package com.guvi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guvi.config.JwtUtil;
import com.guvi.model.OrderModel;
import com.guvi.model.OrderStatus;
import com.guvi.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    // Required only if your SecurityConfig depends on JwtUtil
    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void getAll_ShouldReturnOrders() throws Exception {

        OrderModel order = new OrderModel();
        order.setId("O1");

        when(orderService.getAll())
                .thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("O1"));
    }

    @Test
    void getMyOrders_ShouldReturnOrders() throws Exception {

        OrderModel order = new OrderModel();
        order.setId("O1");

        when(orderService.getMyOrders())
                .thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders/myorders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("O1"));
    }

    @Test
    void getMyOrders_ShouldReturnMessage_WhenEmpty() throws Exception {

        when(orderService.getMyOrders())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/orders/myorders"))
                .andExpect(status().isOk())
                .andExpect(content().string("No orders found"));
    }

    @Test
    void getOrdersByUserId_ShouldReturnOrders() throws Exception {

        OrderModel order = new OrderModel();
        order.setId("O1");

        when(orderService.getByUserId("USER1"))
                .thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders/user/USER1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("O1"));
    }

    @Test
    void getOrdersByUserId_ShouldReturnMessage_WhenNoOrdersFound() throws Exception {

        when(orderService.getByUserId("USER1"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/orders/user/USER1"))
                .andExpect(status().isOk())
                .andExpect(content().string("No orders found for user: USER1"));
    }

    @Test
    void getOrder_ShouldReturnOrder() throws Exception {

        OrderModel order = new OrderModel();
        order.setId("O1");

        when(orderService.getByID("O1"))
                .thenReturn(order);

        mockMvc.perform(get("/api/orders/O1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("O1"));
    }

    @Test
    void create_ShouldReturnCreated() throws Exception {

        String requestBody = """
                                {
                                  "orderedItems": [
                                    {
                                      "productId": "P1",
                                      "quantity": 2
                                    }
                                  ]
                                }
                              """;

        OrderModel created = new OrderModel();
        created.setId("O1");
        created.setStatus(OrderStatus.CONFIRMED);
        created.setOrderedAt(Instant.now());

        when(orderService.create(any()))
                .thenReturn(created);

        mockMvc.perform(post("/api/orders")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("O1"));
    }

    @Test
    void cancel_ShouldReturnCancelledOrder() throws Exception {

        OrderModel cancelled = new OrderModel();
        cancelled.setId("O1");
        cancelled.setStatus(OrderStatus.CANCELLED);

        when(orderService.cancelOrder("O1"))
                .thenReturn(cancelled);

        mockMvc.perform(put("/api/orders/cancel/O1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("O1"))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}