package com.claimflow.insurance.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "policies")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String policyNumber;

    @Column(nullable = false)
    private String policyType; // e.g., "Auto", "Home", "Life", "Health"

    @Column(nullable = false)
    private Double coverageAmount;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    // Many policies can belong to one customer
    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetching for performance
    @JoinColumn(name = "customer_id", nullable = false) // Foreign key column
    private Customer customer;

    public Policy() {
    }

    public Policy(String policyNumber, String policyType, Double coverageAmount, LocalDate startDate, LocalDate endDate, Customer customer) {
        this.policyNumber = policyNumber;
        this.policyType = policyType;
        this.coverageAmount = coverageAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.customer = customer;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public Double getCoverageAmount() {
        return coverageAmount;
    }

    public void setCoverageAmount(Double coverageAmount) {
        this.coverageAmount = coverageAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
