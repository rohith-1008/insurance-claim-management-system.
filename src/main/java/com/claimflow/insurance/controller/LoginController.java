package com.claimflow.insurance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /**
     * Handles GET requests to the /login URL.
     * Returns the logical view name "login", which Spring MVC will resolve to
     * src/main/resources/templates/login.html.
     * This page is permitted access in SecurityConfig.
     * @return The view name for the login page.
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}
