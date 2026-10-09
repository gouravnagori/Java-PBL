package com.legal.repository;

import com.legal.model.LegalReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoRepository interface for LegalReport entities.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module).
 */
@Repository
public interface ReportRepository extends MongoRepository<LegalReport, String> {

    Optional<LegalReport> findByDocumentId(String documentId);

    List<LegalReport> findByUserId(String userId);

    List<LegalReport> findByRiskCategory(String riskCategory);

    void deleteByDocumentId(String documentId);
}
