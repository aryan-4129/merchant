package com.enroll.merchantN;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootTest
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class }) // Skip DB config
@TestPropertySource(properties = {
    "spring.security.oauth2.resourceserver.jwt.issuer-uri=disabled" // Prevent JWT decoder from loading
})
class MerchantNApplicationTests {

    @Test
    void contextLoads() {
        // If application context loads successfully, this test passes.
    }

    // Minimal security config override for tests
    @Configuration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
            return http.build();
        }
    }
}
