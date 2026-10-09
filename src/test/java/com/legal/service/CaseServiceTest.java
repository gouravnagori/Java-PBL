package com.legal.service;

import com.legal.dto.CaseResponse;
import com.legal.dto.CreateCaseRequest;
import com.legal.model.CaseCategory;
import com.legal.model.LegalCase;
import com.legal.model.Role;
import com.legal.model.User;
import com.legal.repository.LegalCaseRepository;
import com.legal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseServiceTest {

    @Mock
    private LegalCaseRepository caseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CaseService caseService;

    private User owner;
    private User intruder;
    private LegalCase legalCase;

    @BeforeEach
    void setUp() {
        owner = new User("Owner", "owner@legal.com", "pass", Role.ROLE_USER);
        owner.setId(1L);

        intruder = new User("Intruder", "intruder@legal.com", "pass", Role.ROLE_USER);
        intruder.setId(2L);

        legalCase = new LegalCase("CASE-2026-001", "NDA Review", "Confidentiality Audit", "Acme Corp",
                CaseCategory.NDA_COMPLIANCE, owner);
        legalCase.setId(100L);
    }

    @Test
    @DisplayName("Should successfully create case for verified user")
    void shouldCreateCase() {
        when(userRepository.findByEmail("owner@legal.com")).thenReturn(Optional.of(owner));
        when(caseRepository.save(any(LegalCase.class))).thenReturn(legalCase);

        CreateCaseRequest request = new CreateCaseRequest();
        request.setTitle("NDA Review");
        request.setDescription("Confidentiality Audit");
        request.setClientName("Acme Corp");
        request.setCategory(CaseCategory.NDA_COMPLIANCE);

        CaseResponse response = caseService.createCase("owner@legal.com", request);

        assertNotNull(response);
        assertEquals("NDA Review", response.getTitle());
        verify(caseRepository, times(1)).save(any(LegalCase.class));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when non-owner attempts to fetch case (Multi-Tenant Isolation)")
    void shouldEnforceMultiTenantIsolation() {
        when(userRepository.findByEmail("intruder@legal.com")).thenReturn(Optional.of(intruder));
        when(caseRepository.findById(100L)).thenReturn(Optional.of(legalCase));

        assertThrows(AccessDeniedException.class, () -> {
            caseService.getCaseById("intruder@legal.com", 100L);
        });
    }
}
