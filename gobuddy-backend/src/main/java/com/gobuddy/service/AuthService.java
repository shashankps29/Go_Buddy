package com.gobuddy.service;

import com.gobuddy.config.JwtUtil;
import com.gobuddy.dto.*;
import com.gobuddy.exception.GoBuddyException;
import com.gobuddy.model.User;
import com.gobuddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new GoBuddyException("Email already registered. Please log in.");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role("user")
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return AuthResponse.builder()
                .token(token)
                .name(user.getName())
                .role(user.getRole())
                .email(user.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new GoBuddyException("Invalid email or password.");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new GoBuddyException("User not found."));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return AuthResponse.builder()
                .token(token)
                .name(user.getName())
                .role(user.getRole())
                .email(user.getEmail())
                .build();
    }
}
