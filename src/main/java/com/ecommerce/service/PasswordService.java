package com.ecommerce.service;


import com.ecommerce.model.PasswordResetToken;
import com.ecommerce.model.User;
import com.ecommerce.repository.PasswordRestTokenRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordRestTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    public PasswordService(UserRepository userRepository, PasswordRestTokenRepository tokenRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }


    @Transactional
    public ResponseEntity<String> forgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        User user = userOptional.get();

        tokenRepository.deleteByUser(user); // this deletes token before creating new one (if exists)
        tokenRepository.flush();

        String token = UUID.randomUUID().toString();


        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(resetToken);

        String resetLink = "http://localhost:8081/reset-password?token=" + token;


       //send email
        String subject = "Password Reset Request";
        String body = "Hello " + user.getName() + ",\n\n" +
                "Click the link below to reset your password:\n" +
                resetLink + "\n\n" +
                "This link will expire in 15 minutes.\n\n" +
                "If you didn’t request a password reset, please ignore this email.";

        emailService.sendEmail(user.getEmail(), subject, body);

        return ResponseEntity.ok("Reset link sent to your email");
    }

    public ResponseEntity<String> resetPassword(String token, String newPassword, String confirmPassword) {

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("Passwords do not match!");
        }

        Optional<PasswordResetToken>  tokenOptional = tokenRepository.findByToken(token);

        if (tokenOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid token!");
        }

        PasswordResetToken resetToken = tokenOptional.get();

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token expired!");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);


        return ResponseEntity.ok("Password reset successfully!");
    }
}
