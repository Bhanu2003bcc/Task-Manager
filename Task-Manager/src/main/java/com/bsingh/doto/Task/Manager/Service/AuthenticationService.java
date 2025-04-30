package com.bsingh.doto.Task.Manager.Service;

import com.bsingh.doto.Task.Manager.DTO.AuthRequest;
import com.bsingh.doto.Task.Manager.DTO.AuthResponse;
import com.bsingh.doto.Task.Manager.Entity.User;
import com.bsingh.doto.Task.Manager.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepo userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthResponse authenticate(AuthRequest authRequest) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword()
                )
        );

        // If authentication succeeds, load the user
        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authRequest.getEmail()));

        // Check if email is verified
        if (!user.isEnabled()) {
            throw new IllegalStateException("Email not verified. Please verify your email first.");
        }

        // Generate JWT token
        String jwtToken = jwtService.generateToken((UserDetails) user);

        // Send login alert email
        try {
            emailService.sendLoginAlertEmail(user.getEmail());
        } catch (Exception e) {
            // Log the error but don't fail authentication
            System.err.println("Failed to send login alert email: " + e.getMessage());
        }

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRoles().stream()
                        .findFirst()
                        .map(role -> role.getName().name())
                        .orElse("USER"))
                .build();
    }
}