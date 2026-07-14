package com.sawai.insurance.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "policies")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Long policyId;

    @NotBlank(message = "Policy number is required")
    @Column(name = "policy_number", nullable = false, unique = true, length = 30)
    private String policyNumber;

    @NotBlank(message = "Policy name is required")
    @Column(name = "policy_name", nullable = false, length = 100)
    private String policyName;

    @Column(name = "policy_type", length = 40)
    private String policyType;

    @NotNull(message = "Premium amount is required")
    @Positive(message = "Premium amount must be positive")
    @Column(name = "premium_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal premiumAmount;

    @Column(name = "coverage_term", length = 30)
    private String coverageTerm;

    @Column(name = "effective_start_date")
    private LocalDate effectiveStartDate;

    // Associated Customer ID - Many policies can belong to one customer
    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    private Customer customer;

    public Policy() {
    }

    public Policy(String policyNumber, String policyName, String policyType, BigDecimal premiumAmount,
                  String coverageTerm, LocalDate effectiveStartDate, Customer customer) {
        this.policyNumber = policyNumber;
        this.policyName = policyName;
        this.policyType = policyType;
        this.premiumAmount = premiumAmount;
        this.coverageTerm = coverageTerm;
        this.effectiveStartDate = effectiveStartDate;
        this.customer = customer;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public String getCoverageTerm() {
        return coverageTerm;
    }

    public void setCoverageTerm(String coverageTerm) {
        this.coverageTerm = coverageTerm;
    }

    public LocalDate getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(LocalDate effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    // Convenience field so the frontend gets the customer id directly in JSON
    @JsonProperty("customerId")
    public Long getCustomerId() {
        return customer != null ? customer.getCustomerId() : null;
    }
}
