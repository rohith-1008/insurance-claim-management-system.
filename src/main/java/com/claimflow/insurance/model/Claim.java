package com.claimflow.insurance.model; // Updated package

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @Column(name = "claim_number", nullable = false, unique = true)
    private String claimNumber;

    @Column(name = "claim_date", nullable = false)
    private LocalDate claimDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "claim_amount", nullable = false)
    private Double claimAmount;

    @Column(nullable = false)
    private String status;

    public Claim() {}

    public Claim(String claimNumber, LocalDate claimDate, String description, Double claimAmount, String status) {
        this.claimNumber = claimNumber;
        this.claimDate = claimDate;
        this.description = description;
        this.claimAmount = claimAmount;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public LocalDate getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDate claimDate) {
        this.claimDate = claimDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(Double claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}