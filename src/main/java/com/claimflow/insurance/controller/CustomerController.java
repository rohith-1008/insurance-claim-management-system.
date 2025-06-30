package com.claimflow.insurance.controller;

import com.claimflow.insurance.model.Customer;
import com.claimflow.insurance.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customers") // Base mapping for all customer-related URLs
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Displays a list of all customers.
     * Accessible at /customers
     */
    @GetMapping
    public String listCustomers(Model model) {
        List<Customer> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "customers/list"; // Resolves to src/main/resources/templates/customers/list.html
    }

    /**
     * Displays the form to add a new customer.
     * Accessible at /customers/new
     */
    @GetMapping("/new")
    public String showAddCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("pageTitle", "Add New Customer");
        return "customers/form"; // Resolves to src/main/resources/templates/customers/form.html
    }

    /**
     * Displays the form to edit an existing customer.
     * Accessible at /customers/edit/{id}
     */
    @GetMapping("/edit/{id}")
    public String showEditCustomerForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        if (customer.isPresent()) {
            model.addAttribute("customer", customer.get());
            model.addAttribute("pageTitle", "Edit Customer (ID: " + id + ")");
            return "customers/form"; // Resolves to src/main/resources/templates/customers/form.html
        } else {
            redirectAttributes.addFlashAttribute("error", "Customer not found with ID: " + id);
            return "redirect:/customers"; // Redirect to list if not found
        }
    }

    /**
     * Processes the submission of the customer form (for both add and edit).
     * Accessible via POST to /customers/save
     */
    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute("customer") Customer customer, RedirectAttributes redirectAttributes) {
        customerService.saveCustomer(customer);
        redirectAttributes.addFlashAttribute("success", "Customer saved successfully!");
        return "redirect:/customers"; // Redirect back to the customer list
    }

    /**
     * Deletes a customer by ID.
     * Accessible via GET (or POST) to /customers/delete/{id}
     * Using GET for simplicity, but POST/DELETE is recommended for real applications.
     */
    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        customerService.deleteCustomer(id);
        redirectAttributes.addFlashAttribute("success", "Customer deleted successfully!");
        return "redirect:/customers"; // Redirect back to the customer list
    }
}
