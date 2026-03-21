package com.gobuddy.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class FeedbackRequest {
    @NotBlank private String message;
    @Min(1) @Max(5) private Integer rating;
}
