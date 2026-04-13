package com.guvi.dto;

import com.guvi.model.Role;

import java.util.List;

public class LogInResponse {
    private String message;
    private String token;
    private String email;
    private List<Role> roles;

    public LogInResponse(String message, String email, List<Role> roles, String token) {
        this.message = message;
        this.email = email;
        this.roles = roles;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
