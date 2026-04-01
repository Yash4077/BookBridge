package com.bookbridge.dto;
import lombok.Data;

@Data
public class AuthRequest {
    private String name;
    private String email;
    private String password;
    private String department;
    private String phone;
}
