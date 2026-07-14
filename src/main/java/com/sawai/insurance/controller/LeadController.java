package com.sawai.insurance.controller;

import com.sawai.insurance.entity.Lead;
import com.sawai.insurance.exception.ResourceNotFoundException;
import com.sawai.insurance.repository.LeadRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadRepository leadRepository;

    public LeadController(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    // GET /api/leads
    @GetMapping
    public List<Lead> getAllLeads() {
        return leadRepository.findAll();
    }

    // GET /api/leads/{id}
    @GetMapping("/{id}")
    public Lead getLeadById(@PathVariable Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
    }

    // GET /api/leads/status/{status}
    @GetMapping("/status/{status}")
    public List<Lead> getLeadsByStatus(@PathVariable String status) {
        return leadRepository.findByLeadStatusIgnoreCase(status);
    }

    // POST /api/leads
    @PostMapping
    public ResponseEntity<Lead> createLead(@Valid @RequestBody Lead lead) {
        Lead saved = leadRepository.save(lead);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/leads/{id}
    @PutMapping("/{id}")
    public Lead updateLead(@PathVariable Long id, @Valid @RequestBody Lead updated) {
        Lead existing = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));

        existing.setProspectName(updated.getProspectName());
        existing.setContactInfo(updated.getContactInfo());
        existing.setReferralSource(updated.getReferralSource());
        existing.setLeadStatus(updated.getLeadStatus());
        existing.setAssignedAgentName(updated.getAssignedAgentName());

        return leadRepository.save(existing);
    }

    // DELETE /api/leads/{id} -> ADMIN only, enforced by AuthInterceptor
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteLead(@PathVariable Long id) {
        Lead existing = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        leadRepository.delete(existing);
        return ResponseEntity.ok(Map.of("message", "Lead deleted successfully"));
    }
}
