package com.gobuddy.controller;

import com.gobuddy.dto.SearchHistoryResponse;
import com.gobuddy.dto.UpdateProfileRequest;
import com.gobuddy.dto.UserProfileResponse;
import com.gobuddy.service.SearchService;
import com.gobuddy.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SearchService searchService;

    /**
     * GET /api/users/profile
     * Returns the logged-in user's profile details.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(Principal principal) {
        UserProfileResponse profile = userService.getProfile(principal.getName());
        return ResponseEntity.ok(profile);
    }

    /**
     * PUT /api/users/profile
     * Body: { name, phoneNumber }
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Principal principal) {
        UserProfileResponse updated = userService.updateProfile(principal.getName(), request);
        return ResponseEntity.ok(updated);
    }

    /**
     * GET /api/users/history
     * Returns the last 10 travel searches of the logged-in user.
     */
    @GetMapping("/history")
    public ResponseEntity<List<SearchHistoryResponse>> getHistory(Principal principal) {
        List<SearchHistoryResponse> history = searchService.getHistory(principal.getName());
        return ResponseEntity.ok(history);
    }
}
