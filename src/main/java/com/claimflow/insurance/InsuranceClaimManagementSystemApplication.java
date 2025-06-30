package com.claimflow.insurance;

import com.claimflow.insurance.model.User;
import com.claimflow.insurance.model.Customer;
import com.claimflow.insurance.model.Policy;
import com.claimflow.insurance.repository.UserRepository;
import com.claimflow.insurance.repository.CustomerRepository;
import com.claimflow.insurance.repository.PolicyRepository;
import com.claimflow.insurance.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List; // <<< ADD THIS IMPORT STATEMENT

@SpringBootApplication
@ComponentScan(basePackages = "com.claimflow.insurance")
public class InsuranceClaimManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsuranceClaimManagementSystemApplication.class, args);
    }

    /**
     * CommandLineRunner bean to insert default test users and customers if they don't exist.
     * This is useful for initial setup and testing without a registration page.
     *
     * @param userRepository Injected UserRepository to save the user.
     * @param customerRepository Injected CustomerRepository to save customers.
     * @param policyRepository Injected PolicyRepository to save policies.
     * @param passwordEncoder Injected PasswordEncoder to hash the password.
     * @return A CommandLineRunner instance.
     */
    @Bean
    public CommandLineRunner createDefaultData(UserRepository userRepository, CustomerRepository customerRepository, PolicyRepository policyRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // --- User Data Creation ---
            if (userRepository.findByUsername("testuser").isEmpty()) {
                User testUser = new User();
                testUser.setUsername("testuser");
                testUser.setPassword(passwordEncoder.encode("password123"));
                testUser.setEmail("test@example.com");
                testUser.setRole("USER");
                userRepository.save(testUser);
                System.out.println("Default user 'testuser' created successfully!");
            } else {
                System.out.println("Default user 'testuser' already exists. Skipping creation.");
            }

            // --- Customer Data Creation ---
            List<Customer> existingCustomers = customerRepository.findAll();
            if (existingCustomers.isEmpty()) {
                Customer alice = customerRepository.save(new Customer("Alice", "Smith", "alice.smith@example.com", "111-222-3333", "123 Main St, Anytown", LocalDate.of(1985, 5, 15)));
                Customer bob = customerRepository.save(new Customer("Bob", "Johnson", "bob.j@example.com", "444-555-6666", "456 Oak Ave, Somewhere", LocalDate.of(1990, 10, 20)));
                Customer charlie = customerRepository.save(new Customer("Charlie", "Brown", "charlie.b@example.com", "777-888-9999", "789 Pine Rd, Nowhere", null));
                System.out.println("Sample customer data created successfully!");

                // Add policies if customers were just created
                if (policyRepository.count() == 0) {
                    policyRepository.save(new Policy("POL-001", "Auto", 50000.00, LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1), alice));
                    policyRepository.save(new Policy("POL-002", "Home", 250000.00, LocalDate.of(2022, 6, 1), LocalDate.of(2025, 6, 1), bob));
                    policyRepository.save(new Policy("POL-003", "Life", 100000.00, LocalDate.of(2024, 3, 1), LocalDate.of(2034, 3, 1), alice));
                    System.out.println("Sample policy data created successfully!");
                } else {
                    System.out.println("Sample policy data already exists. Skipping creation.");
                }
            } else {
                System.out.println("Sample customer data already exists. Skipping creation.");
                // If customers exist, check if policies need to be added.
                if (policyRepository.count() == 0) {
                    // Try to retrieve existing customers to link policies
                    Optional<Customer> aliceOpt = customerRepository.findByEmail("alice.smith@example.com");
                    Optional<Customer> bobOpt = customerRepository.findByEmail("bob.j@example.com");

                    aliceOpt.ifPresent(alice -> policyRepository.save(new Policy("POL-001", "Auto", 50000.00, LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1), alice)));
                    bobOpt.ifPresent(bob -> policyRepository.save(new Policy("POL-002", "Home", 250000.00, LocalDate.of(2022, 6, 1), LocalDate.of(2025, 6, 1), bob)));
                    aliceOpt.ifPresent(alice -> policyRepository.save(new Policy("POL-003", "Life", 100000.00, LocalDate.of(2024, 3, 1), LocalDate.of(2034, 3, 1), alice)));
                    System.out.println("Sample policy data created successfully (linked to existing customers)!");
                } else {
                    System.out.println("Sample policy data already exists. Skipping creation.");
                }
            }
        };
    }
}
