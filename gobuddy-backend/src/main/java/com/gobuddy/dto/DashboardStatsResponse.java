package com.gobuddy.dto;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardStatsResponse {
    private long totalUsers;
    private long totalSearches;
    private long totalFeedbacks;
    private Double averageRating;
}
