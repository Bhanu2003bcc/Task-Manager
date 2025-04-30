package com.bsingh.doto.Task.Manager.Service;


import com.bsingh.doto.Task.Manager.DTO.AuthResponse;
import com.bsingh.doto.Task.Manager.DTO.PasswordResetRequest;
import com.bsingh.doto.Task.Manager.DTO.UserDto;
import com.bsingh.doto.Task.Manager.Entity.Role;
import com.bsingh.doto.Task.Manager.Entity.User;
import com.bsingh.doto.Task.Manager.Entity.VerificationToken;
import com.bsingh.doto.Task.Manager.Exception.ResourceNotFoundException;
import com.bsingh.doto.Task.Manager.Repository.RoleRepo;
import com.bsingh.doto.Task.Manager.Repository.UserRepo;
import com.bsingh.doto.Task.Manager.Repository.VerificationTokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepository;
    private final RoleRepo roleRepository;
    private final PasswordEncoder passwordEncoder;
   // private final JwtService jwtService;
    private final EmailService emailService;
    private final VerificationTokenRepo verificationTokenRepository;

    @Transactional
    public AuthResponse registerUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .enabled(false)
                .build();

        // Assign USER role by default

        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() ->
                               new ResourceNotFoundException("Role not found"));
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));

        User savedUser = userRepository.save(user);



        // Generate verification token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(savedUser);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        verificationToken.setTokenType(VerificationToken.TokenType.EMAIL_VERIFICATION);
        verificationTokenRepository.save(verificationToken);

        // Send verification email
        emailService.sendVerificationEmail(savedUser.getEmail(), token);

        return AuthResponse.builder()
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .role("USER")
                .build();
    }

    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }

    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Delete any existing password reset tokens for this user
        verificationTokenRepository.deleteByUserAndTokenType(
                user, VerificationToken.TokenType.PASSWORD_RESET);

        // Generate new token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        verificationToken.setTokenType(VerificationToken.TokenType.PASSWORD_RESET);
        verificationTokenRepository.save(verificationToken);

        // Send password reset email
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        User user = verificationToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }


}
