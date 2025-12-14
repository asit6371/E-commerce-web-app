package com.ecommerce.bootstrap;

import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) throws Exception {

        String adminEmail = System.getenv("ADMIN_EMAIL");
        String adminPassword = System.getenv("ADMIN_PASSWORD");


        if (adminEmail == null || adminPassword == null) {
            System.out.println("Admin credentials not provided. Skipping admin creation");
            return;
        }

        Optional<User> existingAdmin = userRepository.findByEmail(adminEmail);

        if (existingAdmin.isPresent()) {
            System.out.println("Admin already exists.");
            return;
        }


        User admin = new User();
        admin.setName("Admin");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole("ADMIN");

        userRepository.save(admin);


        System.out.println("Admin user created successfully.");
    }
}
