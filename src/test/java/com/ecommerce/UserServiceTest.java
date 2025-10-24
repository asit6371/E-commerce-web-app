package com.ecommerce;

import com.ecommerce.dto.UserRequestDTO;
import com.ecommerce.model.User;
import com.ecommerce.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testRegisterUser() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setName("Kevin");
        dto.setEmail("kevin@example.com");
        dto.setPassword("1234");


        User user = userService.registerUser(dto);

        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals("Kevin", user.getName());
        Assertions.assertTrue(passwordEncoder.matches("1234", user.getPassword()));
    }

    @Test
    void testLoginUser() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setEmail("kevin@example.com");
        dto.setPassword("1234");

        User user = userService.loginUser(dto);

        Assertions.assertEquals("kevin@example.com", user.getEmail());
        Assertions.assertTrue(passwordEncoder.matches("1234", user.getPassword()));
    }

}
