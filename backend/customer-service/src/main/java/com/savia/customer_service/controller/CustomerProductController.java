package com.savia.customer_service.controller;

import com.savia.customer_service.dto.CreateCustomerProductRequest;
import com.savia.customer_service.dto.CustomerProductResponse;
import com.savia.customer_service.service.CustomerProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-products")
@RequiredArgsConstructor
public class CustomerProductController {

    private final CustomerProductService customerProductService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerProductResponse createCustomerProduct(
            @Valid @RequestBody CreateCustomerProductRequest request
    ) {
        return customerProductService.createCustomerProduct(request);
    }

    @GetMapping("/{id}")
    public CustomerProductResponse getProductById(@PathVariable Long id) {
        return customerProductService.getProductById(id);
    }

    @GetMapping("/customer/{customerId}")
    public List<CustomerProductResponse> getProductsByCustomerId(@PathVariable Long customerId) {
        return customerProductService.getProductsByCustomerId(customerId);
    }
}