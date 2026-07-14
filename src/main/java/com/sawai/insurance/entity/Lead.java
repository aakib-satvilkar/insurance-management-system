package com.sawai.insurance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "leads")
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lead_id")
    private Long leadId;

    @NotBlank(message = "Prospect name is required")
    @Column(name = "prospect_name", nullable = false, length = 100)
    private String prospectName;

    @Column(name = "contact_info", length = 100)
    private String contactInfo;

    @Column(name = "referral_source", length = 60)
    private String referralSource;

    @Column(name = "lead_status", length = 30)
    private String leadStatus = "NEW";

    @Column(name = "assigned_agent_name", length = 60)
    private String assignedAgentName;

    public Lead() {
    }

    public Lead(String prospectName, String contactInfo, String referralSource,
                String leadStatus, String assignedAgentName) {
        this.prospectName = prospectName;
        this.contactInfo = contactInfo;
        this.referralSource = referralSource;
        this.leadStatus = leadStatus;
        this.assignedAgentName = assignedAgentName;
    }

    public Long getLeadId() {
        return leadId;
    }

    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }

    public String getProspectName() {
        return prospectName;
    }

    public void setProspectName(String prospectName) {
        this.prospectName = prospectName;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(String referralSource) {
        this.referralSource = referralSource;
    }

    public String getLeadStatus() {
        return leadStatus;
    }

    public void setLeadStatus(String leadStatus) {
        this.leadStatus = leadStatus;
    }

    public String getAssignedAgentName() {
        return assignedAgentName;
    }

    public void setAssignedAgentName(String assignedAgentName) {
        this.assignedAgentName = assignedAgentName;
    }
}
