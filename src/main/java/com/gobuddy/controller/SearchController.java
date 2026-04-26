package com.gobuddy.controller;

import com.gobuddy.dto.TransportOptionResponse;
import com.gobuddy.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<List<TransportOptionResponse>> search(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "ALL") String type,
            Principal principal) {

        String userEmail = (principal != null) ? principal.getName() : null;
        List<TransportOptionResponse> results = searchService.search(from, to, date, type, userEmail);
        return ResponseEntity.ok(results);
    }
}
