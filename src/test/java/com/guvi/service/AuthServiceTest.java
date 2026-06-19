package com.guvi.service;

import com.guvi.config.JwtUtil;
import com.guvi.dto.LogInRequest;
import com.guvi.dto.LogInResponse;
import com.guvi.dto.SignUpRequest;
import com.guvi.dto.SignUpResponse;
import com.guvi.error.DuplicateEmailException;
import com.guvi.error.InvalidCredentialsException;
import com.guvi.model.Role;
import com.guvi.model.UserModel;
import com.guvi.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private SignUpRequest signUpRequest;
    private LogInRequest logInRequest;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest();
        signUpRequest.setName("Ashik");
        signUpRequest.setEmail("ashik@example.com");
        signUpRequest.setPassword("password123");

        logInRequest = new LogInRequest();
        logInRequest.setEmail("ashik@example.com");
        logInRequest.setPassword("password123");
    }

    @Test
    void signUp_ShouldCreateUserSuccessfully() {

        when(userRepository.existsByEmailIgnoreCase("ashik@example.com"))
                .thenReturn(false);

        UserModel savedUser = new UserModel(
                "1L",
                "Ashik",
                "ashik@example.com",
                List.of(Role.USER, Role.ADMIN),
                true,
                "hashedPassword"
        );

        when(userRepository.save(any(UserModel.class)))
                .thenReturn(savedUser);

        SignUpResponse response = authService.signUp(signUpRequest);

        assertNotNull(response);
        assertEquals("1L", response.getId());
        assertEquals("Ashik", response.getName());
        assertEquals("ashik@example.com", response.getEmail());
        assertTrue(response.isActive());
        assertEquals(2, response.getRoles().size());

        ArgumentCaptor<UserModel> captor =
                ArgumentCaptor.forClass(UserModel.class);

        verify(userRepository).save(captor.capture());

        UserModel userToSave = captor.getValue();

        assertEquals("Ashik", userToSave.getName());
        assertEquals("ashik@example.com", userToSave.getEmail());
        assertTrue(userToSave.isActive());

        // Verify password was encoded
        assertNotEquals("password123", userToSave.getPasswordHash());

        verify(userRepository).existsByEmailIgnoreCase("ashik@example.com");
    }

    @Test
    void signUp_ShouldThrowDuplicateEmailException_WhenEmailExists() {

        when(userRepository.existsByEmailIgnoreCase("ashik@example.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> authService.signUp(signUpRequest)
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void logIn_ShouldReturnToken_WhenCredentialsAreValid() {

        String encodedPassword =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                        .encode("password123");

        UserModel user = new UserModel(
                "1L",
                "Ashik",
                "ashik@example.com",
                List.of(Role.USER),
                true,
                encodedPassword
        );

        when(userRepository.findByEmailIgnoreCase("ashik@example.com"))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken(
                eq("1L"),
                eq(List.of(Role.USER))
        )).thenReturn("jwt-token");

        LogInResponse response = authService.logIn(logInRequest);

        assertNotNull(response);
        assertEquals("Login succesful", response.getMessage());
        assertEquals("ashik@example.com", response.getEmail());
        assertEquals("jwt-token", response.getToken());

        verify(jwtUtil).generateToken(
                "1L",
                List.of(Role.USER)
        );
    }

    @Test
    void logIn_ShouldThrowInvalidCredentialsException_WhenEmailNotFound() {

        when(userRepository.findByEmailIgnoreCase("ashik@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.logIn(logInRequest)
        );

        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    void logIn_ShouldThrowInvalidCredentialsException_WhenPasswordMismatch() {

        String encodedPassword =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                        .encode("differentPassword");

        UserModel user = new UserModel(
                "1L",
                "Ashik",
                "ashik@example.com",
                List.of(Role.USER),
                true,
                encodedPassword
        );

        when(userRepository.findByEmailIgnoreCase("ashik@example.com"))
                .thenReturn(Optional.of(user));

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.logIn(logInRequest)
        );

        verify(jwtUtil, never()).generateToken(any(), any());
    }
}