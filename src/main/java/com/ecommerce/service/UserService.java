package com.ecommerce.service;

import com.ecommerce.dto.UserProfileResponseDto;
import com.ecommerce.dto.UserRequestDto;
import com.ecommerce.exception.EmailAndPasswordNotFoundException;
import com.ecommerce.exception.EmailNotFoundException;
import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public User registerUser(UserRequestDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("USER");

        return userRepository.save(user);
    }


    public User loginUser(UserRequestDto dto) throws EmailAndPasswordNotFoundException {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new EmailAndPasswordNotFoundException("Invalid email or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }

    public UserProfileResponseDto getUserProfile(String email) throws EmailNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("Invalid Email!"));

        UserProfileResponseDto logged = new UserProfileResponseDto();
        logged.setName(user.getName());
        logged.setEmail(user.getEmail());
        logged.setRole(user.getRole());

        return logged;
    }


}
