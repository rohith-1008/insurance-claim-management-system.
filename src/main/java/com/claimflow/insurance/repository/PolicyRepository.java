package com.claimflow.insurance.repository;

import com.claimflow.insurance.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    // Custom query methods for policies
    Optional<Policy> findByPolicyNumber(String policyNumber);
    List<Policy> findByCustomer_Id(Long customerId); // Find policies by customer ID
}
