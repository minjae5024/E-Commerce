package minjae5024.ECommerceProject.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Smart E-Commerce API")
                .version("v1")
                .description("스마트 이커머스 플랫폼 API 문서"));
    }
}