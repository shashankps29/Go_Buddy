package com.gobuddy.dto;
import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserProfileResponse {
    private Long userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String role;
    private LocalDateTime createdAt;
}
