package com.claimflow.insurance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    /**
     * Handles the root URL ("/") and directs to the splash page.
     * This is the very first page the user will see, featuring the project name with animation.
     * @return The name of the splash view template.
     */
    @GetMapping("/")
    public String splash() {
        return "splash"; // Corresponds to src/main/resources/templates/splash.html
    }

    /**
     * Handles the "/home" URL to display the main home page after the splash screen.
     * @return The name of the home view template.
     */
    @GetMapping("/home")
    public String home() {
        return "home"; // Corresponds to src/main/resources/templates/home.html
    }
}
