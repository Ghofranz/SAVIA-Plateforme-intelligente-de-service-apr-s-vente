package com.savia.sav_service.controller;

import com.savia.sav_service.dto.AssignSavCaseRequest;
import com.savia.sav_service.dto.CreateSavCaseRequest;
import com.savia.sav_service.dto.SavCaseResponse;
import com.savia.sav_service.dto.SavCaseStatusHistoryResponse;
import com.savia.sav_service.dto.UpdateSavCaseStatusRequest;
import com.savia.sav_service.enums.SavCaseStatus;
import com.savia.sav_service.security.AuthenticatedUser;
import com.savia.sav_service.service.SavCaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sav-cases")
@RequiredArgsConstructor
public class SavCaseController {

    private final SavCaseService savCaseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SavCaseResponse createSavCase(
            @Valid @RequestBody CreateSavCaseRequest request,
            Authentication authentication
    ) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return savCaseService.createSavCase(request, user.userId());
    }

    @GetMapping("/{id}")
    public SavCaseResponse getSavCaseById(@PathVariable Long id) {
        return savCaseService.getSavCaseById(id);
    }

    @GetMapping("/reference/{caseReference}")
    public SavCaseResponse getSavCaseByReference(@PathVariable String caseReference) {
        return savCaseService.getSavCaseByReference(caseReference);
    }

    @GetMapping("/customer/{customerId}")
    public List<SavCaseResponse> getSavCasesByCustomerId(@PathVariable Long customerId) {
        return savCaseService.getSavCasesByCustomerId(customerId);
    }

    @GetMapping("/product/{customerProductId}")
    public List<SavCaseResponse> getSavCasesByProductId(@PathVariable Long customerProductId) {
        return savCaseService.getSavCasesByProductId(customerProductId);
    }

    @GetMapping("/status/{status}")
    public List<SavCaseResponse> getSavCasesByStatus(@PathVariable SavCaseStatus status) {
        return savCaseService.getSavCasesByStatus(status);
    }

    @PutMapping("/{id}/assign")
    public SavCaseResponse assignSavCase(
            @PathVariable Long id,
            @RequestBody AssignSavCaseRequest request
    ) {
        return savCaseService.assignSavCase(id, request);
    }

    @PutMapping("/{id}/status")
    public SavCaseResponse updateSavCaseStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSavCaseStatusRequest request,
            Authentication authentication
    ) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return savCaseService.updateSavCaseStatus(id, request, user.userId());
    }

    @GetMapping("/{id}/history")
    public List<SavCaseStatusHistoryResponse> getStatusHistory(@PathVariable Long id) {
        return savCaseService.getStatusHistory(id);
    }
}