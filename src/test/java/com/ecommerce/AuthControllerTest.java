package com.ecommerce;


import com.ecommerce.dto.AuthRequestDto;
import com.ecommerce.dto.UserRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterUser() throws Exception {
        UserRequestDto dto = new UserRequestDto();
        dto.setName("SpringUser");
        dto.setEmail("springuser@example.com");
        dto.setPassword("12345");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SpringUser"))
                .andExpect(jsonPath("$.email").value("springuser@example.com"));
    }

    @Test
    void testLoginUser() throws Exception {
        // First register the user
        UserRequestDto registerDto = new UserRequestDto();
        registerDto.setName("Cathy");
        registerDto.setEmail("cathy@example.com");
        registerDto.setPassword("cathy1234");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk());

        // Then login
        AuthRequestDto loginRequest = new AuthRequestDto();
        loginRequest.setEmail("cathy@example.com");
        loginRequest.setPassword("cathy1234");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        AuthRequestDto invalidLogin = new AuthRequestDto();
        invalidLogin.setEmail("unknown@example.com");
        invalidLogin.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLogin)))
                .andExpect(status().isUnauthorized());
    }
}
