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
import io.jsonwebtoken.Jwts;
import org.apache.catalina.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Name;
import java.util.List;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public SignUpResponse signUp(SignUpRequest req) {
        String name = req.getName();
        String email = req.getEmail().toLowerCase();
        String password = req.getPassword();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException(email);
        }
        String passwordHash = bCryptPasswordEncoder.encode(password);

            List<Role> roles =List.of(Role.USER,Role.ADMIN);
        UserModel user = new UserModel(null, name,email,roles,true,passwordHash);
        UserModel savedUser =  userRepository.save(user);
        return new SignUpResponse(savedUser.getId(),savedUser.getName(), savedUser.getEmail(), savedUser.getRoles(),savedUser.isActive());
    }

    public LogInResponse logIn(LogInRequest req){
        String email = req.getEmail().toLowerCase();
        String password = req.getPassword();
        UserModel user = userRepository.findByEmailIgnoreCase(email).orElseThrow(InvalidCredentialsException::new);
        boolean isPasswordMatch = bCryptPasswordEncoder.matches(password,user.getPasswordHash());
        if(!isPasswordMatch) {
            throw new InvalidCredentialsException();
        }
        String token = jwtUtil.generateToken(user.getId(), user.getRoles());
        return new LogInResponse(
                "Login succesful",
                user.getEmail(),
                user.getRoles(),
                token
        );
    }

}
