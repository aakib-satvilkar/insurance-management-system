package com.sawai.insurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Sawai Associates Insurance Management System.
 * Boots the embedded server and wires up all Spring components
 * (controllers, services, repositories, interceptors).
 */
@SpringBootApplication
public class InsuranceManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsuranceManagementSystemApplication.class, args);
    }
}
