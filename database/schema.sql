-- ============================================================
-- Sawai Associates Insurance Management System
-- MySQL schema + seed data
-- ============================================================

CREATE DATABASE IF NOT EXISTS insurance_db;
USE insurance_db;

-- ---------------------------------------------------------
-- Customers
-- ---------------------------------------------------------
DROP TABLE IF EXISTS policies;
DROP TABLE IF EXISTS leads;
DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
    customer_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(50)  NOT NULL,
    last_name       VARCHAR(50)  NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    phone_number    VARCHAR(15),
    date_of_birth   DATE,
    account_status  VARCHAR(20)  DEFAULT 'ACTIVE'
);

-- ---------------------------------------------------------
-- Policies (each policy belongs to exactly one customer)
-- ---------------------------------------------------------
CREATE TABLE policies (
    policy_id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_number         VARCHAR(30)  NOT NULL UNIQUE,
    policy_name           VARCHAR(100) NOT NULL,
    policy_type           VARCHAR(40),
    premium_amount         DECIMAL(12,2) NOT NULL,
    coverage_term          VARCHAR(30),
    effective_start_date   DATE,
    customer_id            BIGINT NOT NULL,
    CONSTRAINT fk_policy_customer
        FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
        ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Leads (sales pipeline, independent of customers table)
-- ---------------------------------------------------------
CREATE TABLE leads (
    lead_id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    prospect_name          VARCHAR(100) NOT NULL,
    contact_info            VARCHAR(100),
    referral_source          VARCHAR(60),
    lead_status              VARCHAR(30) DEFAULT 'NEW',
    assigned_agent_name       VARCHAR(60)
);

-- ---------------------------------------------------------
-- Seed data (for demo / interview walkthrough)
-- ---------------------------------------------------------
INSERT INTO customers (first_name, last_name, email, phone_number, date_of_birth, account_status) VALUES
('Rohan', 'Deshmukh', 'rohan.deshmukh@example.com', '9822011122', '1990-05-14', 'ACTIVE'),
('Ananya', 'Sharma', 'ananya.sharma@example.com', '9822033344', '1988-11-02', 'ACTIVE'),
('Vikram', 'Patil', 'vikram.patil@example.com', '9822055566', '1995-02-20', 'INACTIVE');

INSERT INTO policies (policy_number, policy_name, policy_type, premium_amount, coverage_term, effective_start_date, customer_id) VALUES
('POL-1001', 'Family Health Shield', 'HEALTH', 18500.00, '1 Year', '2026-01-01', 1),
('POL-1002', 'Term Life Secure', 'LIFE', 12500.00, '10 Years', '2025-06-15', 1),
('POL-1003', 'Motor Comprehensive', 'MOTOR', 7200.00, '1 Year', '2026-03-10', 2);

INSERT INTO leads (prospect_name, contact_info, referral_source, lead_status, assigned_agent_name) VALUES
('Suresh Nair', 'suresh.nair@example.com', 'Website Form', 'NEW', 'Agent Priya'),
('Meera Joshi', '9876543210', 'Referral - Existing Customer', 'CONTACTED', 'Agent Priya'),
('Karan Malhotra', 'karan.m@example.com', 'Social Media Ad', 'QUALIFIED', 'Agent Ravi');
