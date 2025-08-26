package minjae5024.ECommerceProject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @GetMapping("/")
    public String home() {
        return "스마트 이커머스 플랫폼 API";
    }
}
