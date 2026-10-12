package com.legal.repository;

import com.legal.model.CaseCategory;
import com.legal.model.CaseStatus;
import com.legal.model.LegalCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LegalCaseRepository extends JpaRepository<LegalCase, Long> {

    List<LegalCase> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<LegalCase> findByIdAndUserId(Long id, Long userId);

    Optional<LegalCase> findByCaseNumberAndUserId(String caseNumber, Long userId);

    List<LegalCase> findByUserIdAndStatus(Long userId, CaseStatus status);

    List<LegalCase> findByUserIdAndCategory(Long userId, CaseCategory category);

    boolean existsByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);
}
