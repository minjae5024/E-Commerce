package com.ecommerce.config;

import com.ecommerce.domain.Product;
import com.ecommerce.domain.Role;
import com.ecommerce.domain.User;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@admin.com";
        if (!userRepository.findByEmail(adminEmail).isPresent()) {
            User admin = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin"))
                    .name("admin")
                    .address("admin")
                    .role(Role.ADMIN)
                    .points(1000000)
                    .build();
            userRepository.save(admin);
        }

        String productName = "testProduct";
        if (productRepository.findByName(productName).isEmpty()) {
            Product product = Product.builder()
                    .name(productName)
                    .price(1000)
                    .stockQuantity(1000)
                    .description("테스트 상품입니다.")
                    .build();
            productRepository.save(product);
        }
    }
}
