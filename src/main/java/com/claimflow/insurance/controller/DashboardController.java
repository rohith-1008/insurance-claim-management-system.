package com.claimflow.insurance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling requests related to the user dashboard.
 */
@Controller
public class DashboardController {

    /**
     * Handles GET requests to the /dashboard URL.
     * This method will be invoked after a successful user login.
     * It returns the logical view name "dashboard", which Thymeleaf will resolve to
     * src/main/resources/templates/dashboard.html.
     * @return The view name for the dashboard page.
     */
    @GetMapping("/dashboard")
    public String showDashboard() {
        // You can add model attributes here to pass data to your dashboard.html
        // For example: model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        return "dashboard";
    }
}
