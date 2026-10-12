package com.legal.service;

import com.legal.dto.CaseResponse;
import com.legal.dto.CreateCaseRequest;
import com.legal.dto.UpdateCaseStatusRequest;
import com.legal.model.CaseStatus;
import com.legal.model.LegalCase;
import com.legal.model.User;
import com.legal.repository.LegalCaseRepository;
import com.legal.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CaseService {

    private final LegalCaseRepository caseRepository;
    private final UserRepository userRepository;

    public CaseService(LegalCaseRepository caseRepository, UserRepository userRepository) {
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CaseResponse createCase(String userEmail, CreateCaseRequest request) {
        User user = userRepository.findByEmail(userEmail.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found."));

        String caseNumber = "CASE-" + java.time.Year.now().getValue() + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        LegalCase legalCase = new LegalCase(
                caseNumber,
                request.getTitle().trim(),
                request.getDescription(),
                request.getClientName(),
                request.getCategory(),
                user
        );

        LegalCase saved = caseRepository.save(legalCase);
        return new CaseResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CaseResponse> getUserCases(String userEmail) {
        User user = userRepository.findByEmail(userEmail.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found."));

        return caseRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(CaseResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CaseResponse getCaseById(String userEmail, Long caseId) {
        User user = userRepository.findByEmail(userEmail.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found."));

        LegalCase legalCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Case with id " + caseId + " does not exist."));

        // Strict Multi-Tenant Ownership Guard
        if (!legalCase.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Unauthorized: You do not own case #" + caseId);
        }

        return new CaseResponse(legalCase);
    }

    @Transactional
    public CaseResponse updateCaseStatus(String userEmail, Long caseId, UpdateCaseStatusRequest request) {
        User user = userRepository.findByEmail(userEmail.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found."));

        LegalCase legalCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Case with id " + caseId + " does not exist."));

        // Strict Multi-Tenant Ownership Guard
        if (!legalCase.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Unauthorized: You cannot modify case #" + caseId);
        }

        legalCase.setStatus(request.getStatus());
        LegalCase updated = caseRepository.save(legalCase);
        return new CaseResponse(updated);
    }

    @Transactional
    public void deleteCase(String userEmail, Long caseId) {
        User user = userRepository.findByEmail(userEmail.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found."));

        LegalCase legalCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Case with id " + caseId + " does not exist."));

        // Strict Multi-Tenant Ownership Guard
        if (!legalCase.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Unauthorized: You cannot delete case #" + caseId);
        }

        caseRepository.delete(legalCase);
    }
}
