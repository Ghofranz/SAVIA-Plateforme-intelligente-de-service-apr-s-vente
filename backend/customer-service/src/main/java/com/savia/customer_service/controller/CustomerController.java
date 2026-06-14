package com.savia.customer_service.controller;

import com.savia.customer_service.dto.CreateCustomerRequest;
import com.savia.customer_service.dto.CustomerResponse;
import com.savia.customer_service.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.savia.customer_service.security.AuthenticatedUser;
import org.springframework.security.core.Authentication;
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createCustomer(
            @Valid @RequestBody CreateCustomerRequest request,
            Authentication authentication
    ) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return customerService.createCustomer(request, user.userId());
    }

    @GetMapping("/{id}")
    public CustomerResponse getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping("/auth-user/{authUserId}")
    public CustomerResponse getCustomerByAuthUserId(@PathVariable Long authUserId) {
        return customerService.getCustomerByAuthUserId(authUserId);
    }

    @GetMapping("/email/{email}")
    public CustomerResponse getCustomerByEmail(@PathVariable String email) {
        return customerService.getCustomerByEmail(email);
    }
}