package com.ecommerce.service;

import com.ecommerce.domain.User;
import com.ecommerce.dto.UserSignupRequestDto;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공 - 정상적인 요청")
    void signup_success() {
        // given
        UserSignupRequestDto requestDto = new UserSignupRequestDto();
        requestDto.setEmail("testEmail@test.com");
        requestDto.setPassword("testPassword");
        requestDto.setName("testName");

        given(userRepository.findByEmail(requestDto.getEmail())).willReturn(Optional.empty());
        given(passwordEncoder.encode(requestDto.getPassword())).willReturn("testEncodedPassword");
        given(userRepository.save(any(User.class))).willReturn(null);

        // when
        userService.signup(requestDto);

        // then
        verify(userRepository).findByEmail(requestDto.getEmail());
        verify(passwordEncoder).encode(requestDto.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_fail_duplicateEmail() {
        // given
        UserSignupRequestDto requestDto = new UserSignupRequestDto();
        requestDto.setEmail("testEmail@test.com");
        requestDto.setPassword("testPassword");
        requestDto.setName("testName");

        given(userRepository.findByEmail(requestDto.getEmail())).willReturn(Optional.of(User.builder().build()));

        // when & then
        assertThatThrownBy(() -> userService.signup(requestDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }
}
