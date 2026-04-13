package com.xk.user.controller;

import com.xk.user.dto.TokenRequest;
import com.xk.user.dto.TokenResponse;
import com.xk.user.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class UserControllerTest {

    private final WebApplicationContext context;
    private final ObjectMapper objectMapper;

    @Autowired
    UserControllerTest(WebApplicationContext context, ObjectMapper objectMapper) {
        this.context = context;
        this.objectMapper = objectMapper;
    }

    private UserDTO createdUser;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        createdUser = createUser();
    }

    @Test
    void testGetToken() throws Exception {
        TokenRequest request = new TokenRequest(
            "password",
            "TestUser",
            "TestPassword",
            "test",
            "test");

        mockMvc.perform(
            post("/users/token")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token_type").value("Bearer"))
            .andExpect(jsonPath("$.scope").value("achievement_system:read"));
    }

    @Test
    void testGetTokenNullPassword() throws Exception {
        TokenRequest request = new TokenRequest(
            "password",
            "TestUser",
            "",
            "test",
            "test");

        mockMvc.perform(
                post("/users/token")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTokenWrongClientId() throws Exception {
        TokenRequest request = new TokenRequest(
            "password",
            "TestUser",
            "TestPassword",
            "testfailed",
            "test");

        mockMvc.perform(
                post("/users/token")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateUserExistingUsername() throws Exception {
        UserDTO user = new UserDTO(UUID.randomUUID().toString(),
            "TestUser",
            "",
            "cultivator");

        mockMvc.perform(
            post("/users/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isNotFound());
    }

    private UserDTO createUser() throws Exception {
        UserDTO user = new UserDTO(UUID.randomUUID().toString(), "TestUser", "TestPassword", "cultivator");

        // Simulate creating a user
        String responseBody = mockMvc.perform(
            post("/users/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(responseBody, UserDTO.class);
    }

}
