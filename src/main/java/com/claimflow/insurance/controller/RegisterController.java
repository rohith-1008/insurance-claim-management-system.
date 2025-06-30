package com.claimflow.insurance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.claimflow.insurance.model.User;
import com.claimflow.insurance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model; // Import Model

@Controller
public class RegisterController {

    private final UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles GET requests to the /register URL to display the registration form.
     * @param model The Model object to add attributes for the view.
     * @return The logical view name "register", which Spring MVC will resolve to
     * src/main/resources/templates/register.html.
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Add a new User object to the model, which will be used by the form
        model.addAttribute("user", new User());
        return "register";
    }

    /**
     * Handles POST requests to the /register URL when the registration form is submitted.
     * This method saves the new user to the database after hashing the password.
     * @param user The User object populated from the form submission.
     * @return A redirect to the login page on successful registration.
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        userService.saveUser(user); // Save the user with the hashed password
        return "redirect:/login?registered"; // Redirect to login page with a success parameter
    }
}
