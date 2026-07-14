package com.sawai.insurance.repository;

import com.sawai.insurance.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    List<Lead> findByLeadStatusIgnoreCase(String leadStatus);

    List<Lead> findByAssignedAgentNameIgnoreCase(String assignedAgentName);
}
