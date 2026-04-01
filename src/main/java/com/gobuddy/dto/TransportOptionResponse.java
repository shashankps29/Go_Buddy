package com.gobuddy.dto;
import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TransportOptionResponse {
    private String type;
    private String providerName;
    private BigDecimal price;
    private String duration;
    private String departureTime;
    private String arrivalTime;
    private String redirectUrl;
    private String from;
    private String to;
}
