package com.savia.customer_service.service;

import com.savia.customer_service.dto.CreateCustomerProductRequest;
import com.savia.customer_service.dto.CustomerProductResponse;
import com.savia.customer_service.entity.Customer;
import com.savia.customer_service.entity.CustomerProduct;
import com.savia.customer_service.exception.ConflictException;
import com.savia.customer_service.exception.ResourceNotFoundException;
import com.savia.customer_service.repository.CustomerProductRepository;
import com.savia.customer_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerProductService {

    private final CustomerProductRepository customerProductRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public CustomerProductResponse createCustomerProduct(
            CreateCustomerProductRequest request,
            Long authUserId
    ) {
        Customer customer = customerRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        if (customerProductRepository.existsBySerialNumber(request.serialNumber())) {
            throw new ConflictException("Serial number is already used");
        }

        CustomerProduct product = CustomerProduct.builder()
                .customer(customer)
                .productName(request.productName())
                .brand(request.brand())
                .model(request.model())
                .serialNumber(request.serialNumber())
                .purchaseDate(request.purchaseDate())
                .warrantyEndDate(request.warrantyEndDate())
                .build();

        CustomerProduct savedProduct = customerProductRepository.save(product);

        return mapToResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public CustomerProductResponse getProductById(Long id) {
        CustomerProduct product = customerProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer product not found"));

        return mapToResponse(product);
    }

    @Transactional(readOnly = true)
    public List<CustomerProductResponse> getProductsByAuthUserId(Long authUserId) {
        Customer customer = customerRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        return customerProductRepository.findByCustomerId(customer.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CustomerProductResponse> getProductsByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found");
        }

        return customerProductRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private CustomerProductResponse mapToResponse(CustomerProduct product) {
        return new CustomerProductResponse(
                product.getId(),
                product.getCustomer().getId(),
                product.getProductName(),
                product.getBrand(),
                product.getModel(),
                product.getSerialNumber(),
                product.getPurchaseDate(),
                product.getWarrantyEndDate(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}