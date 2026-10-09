package com.legal.dto;

public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private Long userId;
    private String name;
    private String email;
    private String role;

    public AuthResponse(String token, Long expiresIn, Long userId, String name, String email, String role) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
    public Long getExpiresIn() { return expiresIn; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
