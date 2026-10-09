package com.legal.controller;

import com.legal.dto.ApiResponse;
import com.legal.dto.CaseResponse;
import com.legal.dto.CreateCaseRequest;
import com.legal.dto.UpdateCaseStatusRequest;
import com.legal.service.CaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
@CrossOrigin(origins = "*")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CaseResponse>> createCase(
            Authentication authentication,
            @Valid @RequestBody CreateCaseRequest request) {
        String email = authentication.getName();
        CaseResponse response = caseService.createCase(email, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Legal case created successfully.", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CaseResponse>>> listCases(Authentication authentication) {
        String email = authentication.getName();
        List<CaseResponse> cases = caseService.getUserCases(email);
        return ResponseEntity.ok(ApiResponse.ok("User legal cases retrieved successfully.", cases));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CaseResponse>> getCase(
            Authentication authentication,
            @PathVariable Long id) {
        String email = authentication.getName();
        CaseResponse response = caseService.getCaseById(email, id);
        return ResponseEntity.ok(ApiResponse.ok("Case retrieved successfully.", response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CaseResponse>> updateStatus(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCaseStatusRequest request) {
        String email = authentication.getName();
        CaseResponse response = caseService.updateCaseStatus(email, id, request);
        return ResponseEntity.ok(ApiResponse.ok("Case status updated successfully.", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCase(
            Authentication authentication,
            @PathVariable Long id) {
        String email = authentication.getName();
        caseService.deleteCase(email, id);
        return ResponseEntity.ok(ApiResponse.ok("Case deleted successfully.", null));
    }
}
