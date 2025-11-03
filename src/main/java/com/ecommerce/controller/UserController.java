package com.ecommerce.controller;


import com.ecommerce.dto.UserProfileResponseDto;
import com.ecommerce.dto.UserRequestDto;
import com.ecommerce.dto.UserResponseDto;
import com.ecommerce.exception.EmailAndPasswordNotFoundException;
import com.ecommerce.exception.EmailNotFoundException;
import com.ecommerce.model.User;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRequestDto dto) {
        User savedUser = userService.registerUser(dto);
        UserResponseDto response = new UserResponseDto(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> getProfile() throws EmailNotFoundException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfileResponseDto profile = userService.getUserProfile(email);

        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestBody UserRequestDto req) throws EmailNotFoundException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.updateUserProfile(email, req);

        return ResponseEntity.ok("Profile updated successfully!");
    }

}
