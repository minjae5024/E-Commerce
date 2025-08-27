package com.ecommerce.controller;

import com.ecommerce.dto.UserSignupRequestDto;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("userSignupRequestDto", new UserSignupRequestDto());
        return "user/signup-form";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute UserSignupRequestDto userSignupRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/signup-form"; // 유효성 검사 실패 시, 폼으로 다시 이동
        }

        try {
            userService.signup(userSignupRequestDto);
        } catch (IllegalStateException e) {
            bindingResult.rejectValue("email", "duplicate", e.getMessage());
            return "user/signup-form";
        }

        return "redirect:/users/login"; // 회원가입 성공 시 로그인 페이지로 리다이렉트
    }
}
