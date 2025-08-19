package minjae5024.ECommerceProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // API 중심이면 CSRF는 나중에 JWT 등으로 교체할 때 다시 켜거나 토큰 기반 CSRF 처리
                .csrf(csrf -> csrf.disable())

                // H2 콘솔(로컬) 사용 시 frameOptions를 sameOrigin으로 설정해 둠
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                // 세션 정책: API 기반이면 STATELESS 권장(추후 JWT 사용 대비)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 경로 허용/차단 규칙
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스 / 문서 / 헬스 등은 전체 허용
                        .requestMatchers(
                                "/", "/index.html",
                                "/static/**", "/assets/**", "/favicon.ico",
                                "/products", "/cart", "/orders",
                                "/swagger-ui/**", "/v3/api-docs/**",
                                "/actuator/health", "/actuator/info"
                        ).permitAll()

                        // 개발 중에는 일단 모든 요청 허용 — 나중에 필요 경로만 인증으로 전환
                        .anyRequest().permitAll()
                )

                // 기본 HTTP Basic은 디버그용(나중에 제거/대체)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * 정적 리소스(이미지, css, js) 등은 Security 필터 자체를 우회시킬 수 있다.
     * - ignoring(): Security filter를 완전히 우회 -> 리소스 처리 비용 절감
     * - 주의: CSRF 보호 대상 등에서 완전히 빠지므로 API 보호와는 별개로 설정
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/static/**", "/images/**", "/css/**", "/js/**", "/favicon.ico");
    }

    /**
     * PasswordEncoder를 미리 등록(향후 사용자 패스워드 처리에 필요)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 개발 편의로 InMemory User 추가(운영에서는 DB 기반 UserDetailsService로 교체)
     */
    @Bean
    public UserDetailsService users(PasswordEncoder passwordEncoder) {
        var admin = User.withUsername("admin")
                .password(passwordEncoder.encode("adminpass"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }
}