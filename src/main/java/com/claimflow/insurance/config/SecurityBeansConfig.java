package com.claimflow.insurance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for security-related beans, primarily the PasswordEncoder.
 * Separating this prevents circular dependencies in the main SecurityConfig.
 */
@Configuration
public class SecurityBeansConfig {

    /**
     * Defines a BCryptPasswordEncoder bean for password hashing.
     * @return An instance of BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
