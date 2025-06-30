package com.claimflow.insurance.controller;

import com.claimflow.insurance.model.Policy;
import com.claimflow.insurance.model.Customer; // Import Customer
import com.claimflow.insurance.service.PolicyService;
import com.claimflow.insurance.service.CustomerService; // Import CustomerService to get list of customers
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/policies") // Base mapping for all policy-related URLs
public class PolicyController {

    private final PolicyService policyService;
    private final CustomerService customerService; // To populate customer dropdown in form

    @Autowired
    public PolicyController(PolicyService policyService, CustomerService customerService) {
        this.policyService = policyService;
        this.customerService = customerService;
    }

    /**
     * Displays a list of all policies.
     * Accessible at /policies
     */
    @GetMapping
    public String listPolicies(Model model) {
        List<Policy> policies = policyService.getAllPolicies();
        model.addAttribute("policies", policies);
        return "policies/list"; // Resolves to src/main/resources/templates/policies/list.html
    }

    /**
     * Displays the form to add a new policy.
     * Accessible at /policies/new
     */
    @GetMapping("/new")
    public String showAddPolicyForm(Model model) {
        model.addAttribute("policy", new Policy());
        model.addAttribute("customers", customerService.getAllCustomers()); // For dropdown
        model.addAttribute("pageTitle", "Add New Policy");
        return "policies/form"; // Resolves to src/main/resources/templates/policies/form.html
    }

    /**
     * Displays the form to edit an existing policy.
     * Accessible at /policies/edit/{id}
     */
    @GetMapping("/edit/{id}")
    public String showEditPolicyForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Policy> policy = policyService.getPolicyById(id);
        if (policy.isPresent()) {
            model.addAttribute("policy", policy.get());
            model.addAttribute("customers", customerService.getAllCustomers()); // For dropdown
            model.addAttribute("pageTitle", "Edit Policy (ID: " + id + ")");
            return "policies/form"; // Resolves to src/main/resources/templates/policies/form.html
        } else {
            redirectAttributes.addFlashAttribute("error", "Policy not found with ID: " + id);
            return "redirect:/policies"; // Redirect to list if not found
        }
    }

    /**
     * Processes the submission of the policy form (for both add and edit).
     * Accessible via POST to /policies/save
     */
    @PostMapping("/save")
    public String savePolicy(@ModelAttribute("policy") Policy policy, RedirectAttributes redirectAttributes) {
        // Ensure the customer associated with the policy is a managed entity
        // If the customer ID is provided in the form, retrieve the full Customer object
        if (policy.getCustomer() != null && policy.getCustomer().getId() != null) {
            customerService.getCustomerById(policy.getCustomer().getId()).ifPresent(policy::setCustomer);
        }
        policyService.savePolicy(policy);
        redirectAttributes.addFlashAttribute("success", "Policy saved successfully!");
        return "redirect:/policies"; // Redirect back to the policy list
    }

    /**
     * Deletes a policy by ID.
     * Accessible via GET (or POST) to /policies/delete/{id}
     * Using GET for simplicity, but POST/DELETE is recommended for real applications.
     */
    @GetMapping("/delete/{id}")
    public String deletePolicy(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        policyService.deletePolicy(id);
        redirectAttributes.addFlashAttribute("success", "Policy deleted successfully!");
        return "redirect:/policies"; // Redirect back to the policy list
    }
}
