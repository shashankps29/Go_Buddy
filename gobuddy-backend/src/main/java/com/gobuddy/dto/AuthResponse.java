package com.gobuddy.dto;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private String name;
    private String role;
    private String email;
}
