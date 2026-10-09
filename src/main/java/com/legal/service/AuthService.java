package com.legal.service;

import com.legal.dto.AuthResponse;
import com.legal.dto.LoginRequest;
import com.legal.dto.SignupRequest;
import com.legal.dto.UserResponse;
import com.legal.model.Role;
import com.legal.model.User;
import com.legal.repository.UserRepository;
import com.legal.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(SignupRequest signupRequest) {
        String normalizedEmail = signupRequest.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("An account with email " + normalizedEmail + " already exists.");
        }

        Role userRole = Role.ROLE_USER;
        if (signupRequest.getRole() != null && !signupRequest.getRole().trim().isEmpty()) {
            try {
                userRole = Role.valueOf(signupRequest.getRole().toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                userRole = Role.ROLE_USER;
            }
        }

        User user = new User(
                signupRequest.getName().trim(),
                normalizedEmail,
                passwordEncoder.encode(signupRequest.getPassword()),
                userRole
        );

        User savedUser = userRepository.save(user);

        // Auto authenticate on registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, signupRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return new AuthResponse(
                jwt,
                tokenProvider.getExpirationMs(),
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
    }

    public AuthResponse login(LoginRequest loginRequest) {
        String normalizedEmail = loginRequest.getEmail().trim().toLowerCase();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid user credentials"));

        return new AuthResponse(
                jwt,
                tokenProvider.getExpirationMs(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
