package com.savia.customer_service.repository;

import com.savia.customer_service.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByAuthUserId(Long authUserId);

    boolean existsByEmail(String email);

    boolean existsByAuthUserId(Long authUserId);
}