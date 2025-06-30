package com.claimflow.insurance.repository;

import com.claimflow.insurance.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // You can add custom query methods here if needed, e.g.:
    Optional<Customer> findByEmail(String email);
    // List<Customer> findByLastNameContainingIgnoreCase(String lastName);
}
