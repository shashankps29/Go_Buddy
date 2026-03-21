package com.gobuddy.controller;

import com.gobuddy.dto.FeedbackRequest;
import com.gobuddy.dto.FeedbackResponse;
import com.gobuddy.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * POST /api/feedback
     * Body: { message, rating }
     * Auth: required
     */
    @PostMapping
    public ResponseEntity<FeedbackResponse> submit(
            @Valid @RequestBody FeedbackRequest request,
            Principal principal) {
        FeedbackResponse response = feedbackService.submit(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/feedback
     * Returns all feedback (admin can also call this).
     */
    @GetMapping
    public ResponseEntity<List<FeedbackResponse>> getAll() {
        return ResponseEntity.ok(feedbackService.getAll());
    }
}
