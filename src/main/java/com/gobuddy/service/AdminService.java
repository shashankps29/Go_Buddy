package com.gobuddy.service;

import com.gobuddy.dto.DashboardStatsResponse;
import com.gobuddy.dto.UserProfileResponse;
import com.gobuddy.exception.GoBuddyException;
import com.gobuddy.repository.FeedbackRepository;
import com.gobuddy.repository.SearchRepository;
import com.gobuddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final SearchRepository searchRepository;
    private final FeedbackRepository feedbackRepository;

    public DashboardStatsResponse getStats() {
        return DashboardStatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalSearches(searchRepository.count())
                .totalFeedbacks(feedbackRepository.count())
                .averageRating(feedbackRepository.findAverageRating())
                .build();
    }

    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(u -> UserProfileResponse.builder()
                        .userId(u.getUserId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .phoneNumber(u.getPhoneNumber())
                        .role(u.getRole())
                        .createdAt(u.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new GoBuddyException("User with ID " + userId + " not found.");
        }
        userRepository.deleteById(userId);
    }
}
