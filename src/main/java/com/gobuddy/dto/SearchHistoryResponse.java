package com.gobuddy.dto;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SearchHistoryResponse {
    private Long searchId;
    private String fromLocation;
    private String toLocation;
    private LocalDate travelDate;
    private LocalDateTime createdAt;
}
