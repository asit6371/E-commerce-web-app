package com.ecommerce;

import com.ecommerce.dto.UserProfileResponseDto;
import com.ecommerce.dto.UserRequestDto;
import com.ecommerce.exception.EmailAndPasswordNotFoundException;
import com.ecommerce.exception.EmailNotFoundException;
import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@Disabled
public class UserFeaturesTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private static final String INITIAL_EMAIL = "mama@example.com";
    private static final String UPDATED_EMAIL = "updatedmama@example.com";

    @BeforeEach
    void cleanUpBefore() {
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    void testRegisterUser() {
        UserRequestDto dto = new UserRequestDto();
        dto.setName("Mama");
        dto.setEmail(INITIAL_EMAIL);
        dto.setPassword("mama");

        User user = userService.registerUser(dto);

        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals("Mama", user.getName());
        Assertions.assertTrue(passwordEncoder.matches("mama", user.getPassword()));
    }

    @Test
    @Order(2)
    void testLoginUser() throws EmailAndPasswordNotFoundException {

        UserRequestDto registerDto = new UserRequestDto();
        registerDto.setName("Mama");
        registerDto.setEmail(INITIAL_EMAIL);
        registerDto.setPassword("mama");
        userService.registerUser(registerDto);

        UserRequestDto loginDto = new UserRequestDto();
        loginDto.setEmail(INITIAL_EMAIL);
        loginDto.setPassword("mama");

        User user = userService.loginUser(loginDto);

        Assertions.assertEquals(INITIAL_EMAIL, user.getEmail());
        Assertions.assertTrue(passwordEncoder.matches("mama", user.getPassword()));
    }

    @Test
    @Order(3)
    void testGetUserProfile() throws EmailNotFoundException {
        UserRequestDto registerDto = new UserRequestDto();
        registerDto.setName("Mama");
        registerDto.setEmail(INITIAL_EMAIL);
        registerDto.setPassword("mama");
        User user = userService.registerUser(registerDto);

        UserProfileResponseDto profile = userService.getUserProfile(INITIAL_EMAIL);

        Assertions.assertNotNull(profile);
        Assertions.assertEquals("Mama", profile.getName());
        Assertions.assertEquals(INITIAL_EMAIL, profile.getEmail());
        Assertions.assertEquals(user.getRole(), profile.getRole());
    }

    @Test
    @Order(4)
    void testUpdateUserProfile() throws EmailNotFoundException, EmailAndPasswordNotFoundException {
        UserRequestDto registerDto = new UserRequestDto();
        registerDto.setName("Mama");
        registerDto.setEmail(INITIAL_EMAIL);
        registerDto.setPassword("mama");
        userService.registerUser(registerDto);

        UserRequestDto updateDto = new UserRequestDto();
        updateDto.setName("Updated Mama");
        updateDto.setEmail(UPDATED_EMAIL);
        updateDto.setPassword("newpassword");

        boolean result = userService.updateUserProfile(INITIAL_EMAIL, updateDto);
        Assertions.assertTrue(result);

        UserProfileResponseDto updatedProfile = userService.getUserProfile(UPDATED_EMAIL);
        Assertions.assertEquals("Updated Mama", updatedProfile.getName());
        Assertions.assertEquals(UPDATED_EMAIL, updatedProfile.getEmail());

        User updatedUser = userService.loginUser(updateDto);
        Assertions.assertTrue(passwordEncoder.matches("newpassword", updatedUser.getPassword()));
    }
}
