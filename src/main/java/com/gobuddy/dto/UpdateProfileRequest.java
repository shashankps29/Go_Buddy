package com.gobuddy.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class UpdateProfileRequest {
    @Size(min=2,max=100) private String name;
    private String phoneNumber;
}
