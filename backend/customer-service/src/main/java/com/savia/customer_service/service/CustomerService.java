package com.savia.customer_service.service;

import com.savia.customer_service.dto.CreateCustomerRequest;
import com.savia.customer_service.dto.CustomerResponse;
import com.savia.customer_service.entity.Customer;
import com.savia.customer_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request, Long authUserId) {
        if (customerRepository.existsByAuthUserId(authUserId)) {
            throw new IllegalArgumentException("Customer already exists for this auth user id");
        }

        if (customerRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already used by another customer");
        }

        Customer customer = Customer.builder()
                .authUserId(authUserId)
                .firstname(request.firstname())
                .lastname(request.lastname())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .address(request.address())
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        return mapToResponse(savedCustomer);
    }
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        return mapToResponse(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByAuthUserId(Long authUserId) {
        Customer customer = customerRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        return mapToResponse(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        return mapToResponse(customer);
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getAuthUserId(),
                customer.getFirstname(),
                customer.getLastname(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getAddress(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}