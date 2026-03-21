package com.gobuddy.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {
    @NotBlank @Size(min=2,max=100) private String name;
    @NotBlank @Email               private String email;
    @NotBlank @Size(min=6)         private String password;
    private String phoneNumber;
}
