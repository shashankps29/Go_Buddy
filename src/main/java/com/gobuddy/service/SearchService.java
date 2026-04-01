package com.gobuddy.service;

import com.gobuddy.dto.SearchHistoryResponse;
import com.gobuddy.dto.TransportOptionResponse;
import com.gobuddy.model.Search;
import com.gobuddy.model.TransportOption;
import com.gobuddy.model.User;
import com.gobuddy.repository.SearchRepository;
import com.gobuddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;
    private final UserRepository userRepository;

    /**
     * Main aggregation method — fetches from all transport APIs (mocked),
     * saves search history if user is authenticated, returns combined results.
     */
    public List<TransportOptionResponse> search(String from, String to,
                                                LocalDate date, String type,
                                                String userEmail) {
        // Save search history when user is logged in
        if (userEmail != null) {
            userRepository.findByEmail(userEmail).ifPresent(user -> {
                Search search = Search.builder()
                        .user(user)
                        .fromLocation(from)
                        .toLocation(to)
                        .travelDate(date)
                        .build();
                searchRepository.save(search);
            });
        }

        List<TransportOptionResponse> results = new ArrayList<>();

        if (type == null || type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("BUS")) {
            results.addAll(fetchBusOptions(from, to, date));
        }
        if (type == null || type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("TRAIN")) {
            results.addAll(fetchTrainOptions(from, to, date));
        }
        if (type == null || type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("FLIGHT")) {
            results.addAll(fetchFlightOptions(from, to, date));
        }

        // Sort by price ascending
        results.sort(Comparator.comparing(TransportOptionResponse::getPrice));
        return results;
    }

    /** Simulates RedBus API response */
    private List<TransportOptionResponse> fetchBusOptions(String from, String to, LocalDate date) {
        return List.of(
            buildOption("BUS", "RedBus Express",    new BigDecimal("450"),  "8h 30m", "06:00", "14:30", "https://www.redbus.in", from, to),
            buildOption("BUS", "VRL Travels",        new BigDecimal("650"),  "9h 00m", "22:00", "07:00", "https://www.redbus.in", from, to),
            buildOption("BUS", "SRS Travels",        new BigDecimal("550"),  "8h 45m", "08:30", "17:15", "https://www.redbus.in", from, to),
            buildOption("BUS", "Orange Travels",     new BigDecimal("380"),  "9h 15m", "21:00", "06:15", "https://www.redbus.in", from, to)
        );
    }

    /** Simulates IRCTC API response */
    private List<TransportOptionResponse> fetchTrainOptions(String from, String to, LocalDate date) {
        return List.of(
            buildOption("TRAIN", "Rajdhani Express (12951)", new BigDecimal("1200"), "16h 35m", "16:55", "09:30", "https://www.irctc.co.in", from, to),
            buildOption("TRAIN", "Shatabdi Express (12001)", new BigDecimal("850"),  "8h 15m",  "06:00", "14:15", "https://www.irctc.co.in", from, to),
            buildOption("TRAIN", "Duronto Express (12213)",  new BigDecimal("975"),  "14h 50m", "23:05", "13:55", "https://www.irctc.co.in", from, to),
            buildOption("TRAIN", "Garib Rath (12909)",       new BigDecimal("620"),  "17h 25m", "15:30", "08:55", "https://www.irctc.co.in", from, to)
        );
    }

    /** Simulates Skyscanner / flight API response */
    private List<TransportOptionResponse> fetchFlightOptions(String from, String to, LocalDate date) {
        return List.of(
            buildOption("FLIGHT", "IndiGo 6E-201",  new BigDecimal("3500"), "2h 05m", "06:15", "08:20", "https://www.goindigo.in",   from, to),
            buildOption("FLIGHT", "Air India AI-101", new BigDecimal("4200"), "2h 10m", "08:30", "10:40", "https://www.airindia.in", from, to),
            buildOption("FLIGHT", "SpiceJet SG-123", new BigDecimal("2950"), "2h 20m", "13:00", "15:20", "https://www.spicejet.com", from, to),
            buildOption("FLIGHT", "Vistara UK-801",  new BigDecimal("5100"), "2h 00m", "17:45", "19:45", "https://www.airvistara.com", from, to)
        );
    }

    private TransportOptionResponse buildOption(String type, String provider,
                                                BigDecimal price, String duration,
                                                String dep, String arr,
                                                String url, String from, String to) {
        return TransportOptionResponse.builder()
                .type(type)
                .providerName(provider)
                .price(price)
                .duration(duration)
                .departureTime(dep)
                .arrivalTime(arr)
                .redirectUrl(url)
                .from(from)
                .to(to)
                .build();
    }

    /** Returns last 10 searches for a user */
    public List<SearchHistoryResponse> getHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return searchRepository.findByUserUserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
                .limit(10)
                .map(s -> SearchHistoryResponse.builder()
                        .searchId(s.getSearchId())
                        .fromLocation(s.getFromLocation())
                        .toLocation(s.getToLocation())
                        .travelDate(s.getTravelDate())
                        .createdAt(s.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
