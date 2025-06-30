package com.claimflow.insurance.service;

import com.claimflow.insurance.model.Customer;
import com.claimflow.insurance.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // Get all customers
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Get customer by ID
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    // Save a new customer or update an existing one
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    // Delete a customer by ID
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    // You can add more business logic methods here as needed
    // For example, validation before saving, integration with other services, etc.
}
