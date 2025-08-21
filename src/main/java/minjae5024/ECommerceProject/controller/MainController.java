package minjae5024.ECommerceProject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @GetMapping("/")
    public String home() {
        return "E-Commerce API 서버가 실행 중입니다 🚀";
    }
}