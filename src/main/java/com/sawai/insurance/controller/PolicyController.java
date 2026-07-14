package com.sawai.insurance.controller;

import com.sawai.insurance.entity.Customer;
import com.sawai.insurance.entity.Policy;
import com.sawai.insurance.exception.ResourceNotFoundException;
import com.sawai.insurance.repository.CustomerRepository;
import com.sawai.insurance.repository.PolicyRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;

    public PolicyController(PolicyRepository policyRepository, CustomerRepository customerRepository) {
        this.policyRepository = policyRepository;
        this.customerRepository = customerRepository;
    }

    // GET /api/policies
    @GetMapping
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    // GET /api/policies/{id}
    @GetMapping("/{id}")
    public Policy getPolicyById(@PathVariable Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
    }

    // GET /api/policies/customer/{customerId}  -> all policies for a given customer
    @GetMapping("/customer/{customerId}")
    public List<Policy> getPoliciesByCustomer(@PathVariable Long customerId) {
        return policyRepository.findByCustomer_CustomerId(customerId);
    }

    // POST /api/policies
    @PostMapping
    public ResponseEntity<Policy> createPolicy(@Valid @RequestBody PolicyRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.customerId()));

        Policy policy = new Policy(
                request.policyNumber(),
                request.policyName(),
                request.policyType(),
                request.premiumAmount(),
                request.coverageTerm(),
                request.effectiveStartDate(),
                customer
        );

        Policy saved = policyRepository.save(policy);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/policies/{id}
    @PutMapping("/{id}")
    public Policy updatePolicy(@PathVariable Long id, @Valid @RequestBody PolicyRequest request) {
        Policy existing = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.customerId()));

        existing.setPolicyNumber(request.policyNumber());
        existing.setPolicyName(request.policyName());
        existing.setPolicyType(request.policyType());
        existing.setPremiumAmount(request.premiumAmount());
        existing.setCoverageTerm(request.coverageTerm());
        existing.setEffectiveStartDate(request.effectiveStartDate());
        existing.setCustomer(customer);

        return policyRepository.save(existing);
    }

    // DELETE /api/policies/{id} -> ADMIN only, enforced by AuthInterceptor
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePolicy(@PathVariable Long id) {
        Policy existing = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
        policyRepository.delete(existing);
        return ResponseEntity.ok(Map.of("message", "Policy deleted successfully"));
    }

    /**
     * DTO used for create/update requests so the client sends a plain
     * customerId instead of a nested Customer object.
     */
    public record PolicyRequest(
            String policyNumber,
            String policyName,
            String policyType,
            java.math.BigDecimal premiumAmount,
            String coverageTerm,
            java.time.LocalDate effectiveStartDate,
            Long customerId
    ) {
    }
}
