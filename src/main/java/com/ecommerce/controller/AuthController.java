package com.ecommerce.controller;

import com.ecommerce.config.JwtUtil;
import com.ecommerce.dto.AuthRequestDto;
import com.ecommerce.dto.AuthResponseDto;
import com.ecommerce.service.PasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final PasswordService passwordService;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil, UserDetailsService uds, PasswordService passwordService) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = uds;
        this.passwordService = passwordService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDto req) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthResponseDto(token));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        return passwordService.forgotPassword(email);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
                                                @RequestParam String newPassword,
                                                @RequestParam String confirmPassword) {
        return passwordService.resetPassword(token, newPassword, confirmPassword);
    }

}