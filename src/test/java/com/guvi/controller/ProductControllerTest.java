package com.guvi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guvi.config.JwtUtil;
import com.guvi.model.ProductModel;
import com.guvi.model.ProductStatus;
import com.guvi.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void getAll_ShouldReturnProducts() throws Exception {

        ProductModel product = new ProductModel();
        product.setId("P1");
        product.setName("Laptop");

        when(productService.getAll(
                any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(product));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("P1"))
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    void getAll_WithFilters_ShouldReturnProducts() throws Exception {

        ProductModel product = new ProductModel();
        product.setId("P1");

        when(productService.getAll(
                any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(product));

        mockMvc.perform(get("/api/products")
                        .param("name", "Laptop")
                        .param("categories", "Electronics")
                        .param("sort", "asc")
                        .param("lowStock", "5")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getActive_ShouldReturnActiveProducts() throws Exception {

        ProductModel product = new ProductModel();
        product.setId("P1");
        product.setStatus(ProductStatus.ACTIVE);

        when(productService.getActive())
                .thenReturn(List.of(product));

        mockMvc.perform(get("/api/products/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("P1"));
    }

    @Test
    void create_ShouldReturnCreated() throws Exception {

        String requestBody = """
        {
          "name":"Laptop",
          "description":"Gaming Laptop",
          "price":50000,
          "stockQuantity":10,
          "categories":["Electronics"],
          "status":"ACTIVE"
        }
        """;

        ProductModel created = new ProductModel();
        created.setId("P1");
        created.setName("Laptop");

        when(productService.create(any()))
                .thenReturn(created);

        mockMvc.perform(post("/api/products")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("P1"))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void update_ShouldReturnUpdatedProduct() throws Exception {

        String requestBody = """
        {
          "name":"Laptop Updated",
          "description":"Updated Description",
          "price":60000,
          "stockQuantity":20,
          "categories":["Electronics"],
          "status":"ACTIVE"
        }
        """;

        ProductModel updated = new ProductModel();
        updated.setId("P1");
        updated.setName("Laptop Updated");

        when(productService.update(eq("P1"), any()))
                .thenReturn(updated);

        mockMvc.perform(put("/api/products/P1")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("P1"))
                .andExpect(jsonPath("$.name").value("Laptop Updated"));
    }
}