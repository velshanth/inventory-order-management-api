package com.guvi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guvi.config.JwtUtil;
import com.guvi.dto.CreateCategoryRequest;
import com.guvi.model.CategoryModel;
import com.guvi.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void create_ShouldReturnCreated() throws Exception {

        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Electronics");
        request.setActive(true);

        CategoryModel category =
                new CategoryModel("1", "Electronics", true);

        when(categoryService.create(any(CreateCategoryRequest.class)))
                .thenReturn(category);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void getAll_ShouldReturnCategories() throws Exception {

        List<CategoryModel> categories = List.of(
                new CategoryModel("1", "Electronics", true),
                new CategoryModel("2", "Books", true)
        );

        when(categoryService.getAll()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getActive_ShouldReturnActiveCategories() throws Exception {

        List<CategoryModel> categories = List.of(
                new CategoryModel("1", "Electronics", true)
        );

        when(categoryService.getActive()).thenReturn(categories);

        mockMvc.perform(get("/api/categories/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Electronics"));
    }

    @Test
    void update_ShouldReturnUpdatedCategory() throws Exception {

        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Electronics");
        request.setActive(false);

        CategoryModel updated =
                new CategoryModel("1", "Electronics", false);

        when(categoryService.update(
                eq("1"),
                any(CreateCategoryRequest.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {

        doNothing().when(categoryService).delete("1");

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());

        // Optional verification
        // verify(categoryService).delete("1");
    }
}