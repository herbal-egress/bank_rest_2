package com.example.bankcards.dto;
public class TokenResponseDTO {
    private String token; 
    private String username; 
    private String role; 
    public TokenResponseDTO(String token) {
        this.token = token;
    }
    public TokenResponseDTO(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}