package com.claimflow.insurance.service;

import com.claimflow.insurance.model.Customer;
import com.claimflow.insurance.model.Policy;
import com.claimflow.insurance.repository.CustomerRepository;
import com.claimflow.insurance.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository; // To associate policies with customers

    @Autowired
    public PolicyService(PolicyRepository policyRepository, CustomerRepository customerRepository) {
        this.policyRepository = policyRepository;
        this.customerRepository = customerRepository;
    }

    // Get all policies
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    // Get policy by ID
    public Optional<Policy> getPolicyById(Long id) {
        return policyRepository.findById(id);
    }

    // Save a new policy or update an existing one
    // Ensure the customer exists before saving the policy
    public Policy savePolicy(Policy policy) {
        // Important: Ensure the Customer object within the Policy is managed (exists in DB)
        // If the customer object within policy has an ID, Spring Data JPA will manage it.
        // If it's a new customer, save it first, or retrieve it from DB.
        if (policy.getCustomer() != null && policy.getCustomer().getId() == null) {
            // This scenario might happen if you're trying to create a new customer AND policy at once.
            // For simplicity here, we assume customer is selected from existing ones.
            // If new customer creation is needed, you'd save the customer first:
            // Customer newCustomer = customerRepository.save(policy.getCustomer());
            // policy.setCustomer(newCustomer);
        } else if (policy.getCustomer() != null && policy.getCustomer().getId() != null) {
            // Ensure the customer reference is a managed entity
            Optional<Customer> existingCustomer = customerRepository.findById(policy.getCustomer().getId());
            existingCustomer.ifPresent(policy::setCustomer);
            // If customer not found, you might want to throw an exception or handle it
        }
        return policyRepository.save(policy);
    }

    // Delete a policy by ID
    public void deletePolicy(Long id) {
        policyRepository.deleteById(id);
    }

    // Find policies by customer ID
    public List<Policy> getPoliciesByCustomerId(Long customerId) {
        return policyRepository.findByCustomer_Id(customerId);
    }
}
