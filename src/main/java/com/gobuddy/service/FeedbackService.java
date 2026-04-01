package com.gobuddy.service;

import com.gobuddy.dto.FeedbackRequest;
import com.gobuddy.dto.FeedbackResponse;
import com.gobuddy.exception.GoBuddyException;
import com.gobuddy.model.Feedback;
import com.gobuddy.model.User;
import com.gobuddy.repository.FeedbackRepository;
import com.gobuddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public FeedbackResponse submit(String email, FeedbackRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GoBuddyException("User not found."));

        Feedback feedback = Feedback.builder()
                .user(user)
                .message(request.getMessage())
                .rating(request.getRating())
                .build();

        Feedback saved = feedbackRepository.save(feedback);

        return FeedbackResponse.builder()
                .feedbackId(saved.getFeedbackId())
                .userName(user.getName())
                .message(saved.getMessage())
                .rating(saved.getRating())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    public List<FeedbackResponse> getAll() {
        return feedbackRepository.findAll()
                .stream()
                .map(f -> FeedbackResponse.builder()
                        .feedbackId(f.getFeedbackId())
                        .userName(f.getUser().getName())
                        .message(f.getMessage())
                        .rating(f.getRating())
                        .createdAt(f.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
