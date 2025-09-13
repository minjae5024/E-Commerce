package com.ecommerce.controller;

import com.ecommerce.domain.User;
import com.ecommerce.dto.UserLoginRequestDto;
import com.ecommerce.dto.UserSignupRequestDto;
import com.ecommerce.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 API 성공")
    void signup_success() throws Exception {
        // given
        UserSignupRequestDto requestDto = new UserSignupRequestDto();
        requestDto.setEmail("testEmail@test.com");
        requestDto.setPassword("testPassword");
        requestDto.setName("testName");

        String requestBody = objectMapper.writeValueAsString(requestDto);

        // when
        ResultActions result = mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isCreated())
                .andDo(print());

        User savedUser = userRepository.findByEmail("testEmail@test.com").orElseThrow();
        assertThat(passwordEncoder.matches("testPassword", savedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("회원가입 API 실패 - 이메일 중복")
    void signup_fail_duplicateEmail() throws Exception {
        // given
        UserSignupRequestDto initialRequest = new UserSignupRequestDto();
        initialRequest.setEmail("testEmail@test.com");
        initialRequest.setPassword("testPassword");
        initialRequest.setName("testName");
        mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(initialRequest)));

        UserSignupRequestDto duplicateRequest = new UserSignupRequestDto();
        duplicateRequest.setEmail("testEmail@test.com");
        duplicateRequest.setPassword("anotherPassword");
        duplicateRequest.setName("anotherName");
        String requestBody = objectMapper.writeValueAsString(duplicateRequest);

        // when
        ResultActions result = mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isConflict())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 API 성공")
    void login_success() throws Exception {
        // given
        UserSignupRequestDto signupRequest = new UserSignupRequestDto();
        signupRequest.setEmail("testEmail@test.com");
        signupRequest.setPassword("testPassword");
        signupRequest.setName("testName");
        mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)));

        UserLoginRequestDto loginRequest = new UserLoginRequestDto();
        loginRequest.setEmail("testEmail@test.com");
        loginRequest.setPassword("testPassword");
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        // when
        ResultActions result = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 API 실패 - 잘못된 비밀번호")
    void login_fail_wrongPassword() throws Exception {
        // given
        UserSignupRequestDto signupRequest = new UserSignupRequestDto();
        signupRequest.setEmail("testEmail@test.com");
        signupRequest.setPassword("testPassword");
        signupRequest.setName("testName");
        mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)));

        UserLoginRequestDto loginRequest = new UserLoginRequestDto();
        loginRequest.setEmail("testEmail@test.com");
        loginRequest.setPassword("wrongPassword");
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        // when
        ResultActions result = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isUnauthorized())
                .andDo(print());
    }
}