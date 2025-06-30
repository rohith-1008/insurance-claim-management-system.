package com.claimflow.insurance.service;

import com.claimflow.insurance.model.Claim;
import com.claimflow.insurance.model.Policy;
import com.claimflow.insurance.repository.ClaimRepository;
import com.claimflow.insurance.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private PolicyRepository policyRepository; // To find the policy for a claim

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public Optional<Claim> getClaimById(Long id) {
        return claimRepository.findById(id);
    }

    public Claim saveClaim(Long policyId, Claim claim) {
        Optional<Policy> policyOptional = policyRepository.findById(policyId);
        if (policyOptional.isPresent()) {
            claim.setPolicy(policyOptional.get());
            return claimRepository.save(claim);
        } else {
            throw new RuntimeException("Policy not found with id: " + policyId);
        }
    }

    public void deleteClaim(Long id) {
        claimRepository.deleteById(id);
    }

    public Claim updateClaim(Long id, Claim updatedClaim) {
        return claimRepository.findById(id).map(claim -> {
            claim.setClaimNumber(updatedClaim.getClaimNumber());
            claim.setClaimDate(updatedClaim.getClaimDate());
            claim.setDescription(updatedClaim.getDescription());
            claim.setClaimAmount(updatedClaim.getClaimAmount());
            claim.setStatus(updatedClaim.getStatus());
            // Note: Policy for a claim is typically not changed after creation
            return claimRepository.save(claim);
        }).orElseThrow(() -> new RuntimeException("Claim not found with id " + id));
    }
}