package com.gobuddy.dto;
import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FeedbackResponse {
    private Long feedbackId;
    private String userName;
    private String message;
    private Integer rating;
    private LocalDateTime createdAt;
}
