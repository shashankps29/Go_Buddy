package com.gobuddy.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "transport_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "search_id")
    private Search search;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransportType type;

    @Column(nullable = false, length = 100)
    private String providerName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 50)
    private String duration;

    @Column(length = 10)
    private String departureTime;

    @Column(length = 10)
    private String arrivalTime;

    @Column(nullable = false, length = 255)
    private String redirectUrl;

    public enum TransportType { BUS, TRAIN, FLIGHT }
}
