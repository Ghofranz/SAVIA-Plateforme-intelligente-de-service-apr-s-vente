package com.savia.customer_service.repository;

import com.savia.customer_service.entity.CustomerProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerProductRepository extends JpaRepository<CustomerProduct, Long> {

    List<CustomerProduct> findByCustomerId(Long customerId);

    Optional<CustomerProduct> findBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);
}