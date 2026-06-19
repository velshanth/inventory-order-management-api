package com.guvi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guvi.config.JwtUtil;
import com.guvi.dto.LogInRequest;
import com.guvi.dto.LogInResponse;
import com.guvi.dto.SignUpRequest;
import com.guvi.dto.SignUpResponse;
import com.guvi.model.Role;
import com.guvi.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void signUp_ShouldReturnCreated() throws Exception {

        SignUpRequest request = new SignUpRequest();
        request.setName("Ashik");
        request.setEmail("ashik@test.com");
        request.setPassword("password123");

        SignUpResponse response = new SignUpResponse(
                "1L",
                "Ashik",
                "ashik@test.com",
                List.of(Role.USER),
                true
        );

        when(authService.signUp(any(SignUpRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1L"))
                .andExpect(jsonPath("$.name").value("Ashik"))
                .andExpect(jsonPath("$.email").value("ashik@test.com"));
    }

    @Test
    void login_ShouldReturnOk() throws Exception {

        LogInRequest request = new LogInRequest();
        request.setEmail("ashik@test.com");
        request.setPassword("password123");

        LogInResponse response = new LogInResponse(
                "Login succesful",
                "ashik@test.com",
                List.of(Role.USER),
                "jwt-token"
        );

        when(authService.logIn(any(LogInRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login succesful"))
                .andExpect(jsonPath("$.email").value("ashik@test.com"))
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }
}