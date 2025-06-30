package com.claimflow.insurance.controller;

import com.claimflow.insurance.model.Claim;
import com.claimflow.insurance.service.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims") // Base path for claims API endpoints
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    @GetMapping
    public List<Claim> getAllClaims() {
        return claimService.getAllClaims();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Claim> getClaimById(@PathVariable Long id) {
        return claimService.getClaimById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // To submit a claim, we need the policy ID to associate it with
    @PostMapping("/{policyId}")
    public ResponseEntity<Claim> createClaim(@PathVariable Long policyId, @RequestBody Claim claim) {
        try {
            Claim newClaim = claimService.saveClaim(policyId, claim);
            return ResponseEntity.ok(newClaim);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); // Or a more specific error response
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Claim> updateClaim(@PathVariable Long id, @RequestBody Claim claim) {
        try {
            Claim updatedClaim = claimService.updateClaim(id, claim);
            return ResponseEntity.ok(updatedClaim);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClaim(@PathVariable Long id) {
        try {
            claimService.deleteClaim(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}