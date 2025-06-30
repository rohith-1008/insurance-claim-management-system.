package com.claimflow.insurance.config;

import com.claimflow.insurance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer; // Re-import this
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Main Spring Security configuration for the application.
 * Configures request authorization, form-based login, and logout for a Thymeleaf-based web app.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService; // Changed to UserService as it's sufficient for Thymeleaf
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Defines custom security configuration to ignore requests for static resources
     * from the Spring Security filter chain. This helps improve performance for static assets.
     * Also ignores the initial public pages to prevent circular redirects.
     *
     * @return A WebSecurityCustomizer bean that configures WebSecurity.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            // Ignore static assets completely:
            AntPathRequestMatcher.antMatcher("/css/**"),
            AntPathRequestMatcher.antMatcher("/js/**"),
            AntPathRequestMatcher.antMatcher("/images/**"), // Ensure logo and other images are ignored
            // Ignore specific public GET requests so Spring Security doesn't try to secure them
            // before authentication, preventing redirect loops.
            AntPathRequestMatcher.antMatcher("/"), // The splash page
            AntPathRequestMatcher.antMatcher("/home"), // The main home page
            AntPathRequestMatcher.antMatcher("/login"),
            AntPathRequestMatcher.antMatcher("/register")
        );
    }

    /**
     * Configures the security filter chain.
     * Defines which requests are permitted, required authentication, login, and logout behavior.
     * @param http The HttpSecurity object to configure.
     * @return The built SecurityFilterChain.
     * @throws Exception if configuration fails.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for simplicity in development; for production, ensure it's enabled!
            .csrf(csrf -> csrf.disable())

            // Configure authorization for HTTP requests that *do* pass through the filter chain
            .authorizeHttpRequests(auth -> auth
                // These are routes that *can* be secured by the filter chain if not ignored by webSecurityCustomizer,
                // but specifically permit the login POST request.
                .requestMatchers(AntPathRequestMatcher.antMatcher("/perform_login")).permitAll()
                // All other requests require authentication
                // This catches /dashboard, /customers/**, /policies/** etc.
                .anyRequest().authenticated()
            )
            // Configure form-based login
            .formLogin(form -> form
                .loginPage("/login")               // Specifies the URL for the custom login page
                .loginProcessingUrl("/perform_login") // URL where the login form will submit its credentials (POST)
                .defaultSuccessUrl("/dashboard", true) // Redirect URL after successful login (always redirect)
                .failureUrl("/login?error")        // Redirect URL if login fails (with an error parameter)
                .permitAll()                       // This .permitAll() refers to the login form itself being accessible
            )
            // OAuth2 Login (if you want to re-enable, uncomment and ensure application.properties has credentials)
            // .oauth2Login(oauth2 -> oauth2
            //     .loginPage("/login")
            //     .defaultSuccessUrl("/dashboard", true)
            //     .failureUrl("/login?oauth2_error")
            // )
            // Configure logout
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // URL to trigger logout
                .logoutSuccessUrl("/login?logout") // Redirect URL after logging out (with a logout parameter)
                .invalidateHttpSession(true)       // Invalidate the HttpSession
                .deleteCookies("JSESSIONID")       // Delete the JSESSIONID cookie
                .permitAll()
            );
        return http.build();
    }

    /**
     * Configures the DaoAuthenticationProvider, which uses the UserService
     * to load user details and the PasswordEncoder for password validation.
     * @return The configured DaoAuthenticationProvider.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService); // Uses UserService
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * Exposes the AuthenticationManager bean, which is used internally by Spring Security to perform authentication.
     * @param authenticationConfiguration Provides access to the global AuthenticationConfiguration.
     * @return The AuthenticationManager.
     * @throws Exception if an error occurs during manager retrieval.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
