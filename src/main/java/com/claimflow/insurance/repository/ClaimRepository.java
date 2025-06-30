package com.claimflow.insurance.repository; // Updated package

import com.claimflow.insurance.model.Claim; // Updated import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    // You can add custom query methods here, e.g., findByPolicy_Id(Long policyId);
}