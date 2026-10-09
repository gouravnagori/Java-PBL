package com.legal.repository;

import com.legal.model.Risk;
import com.legal.model.enums.Severity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MongoDB Repository for the Risk entity.
 * Owned by Dilip Kumawat — Legal Analysis & Risk Detection Module.
 */
@Repository
public interface RiskRepository extends MongoRepository<Risk, String> {

    /** Retrieve all risk flags for an analysis run ordered by severity (descending). */
    List<Risk> findByAnalysisIdOrderBySeverityDesc(String analysisId);

    /** Retrieve HIGH and CRITICAL risk highlights for a document (for summary cards). */
    List<Risk> findByAnalysisIdAndSeverityIn(String analysisId, List<Severity> severities);

    /** Retrieve all risks for a specific clause. */
    List<Risk> findByClauseId(String clauseId);

    /** Count all risk flags for a given analysis run. */
    long countByAnalysisId(String analysisId);

    /** Count risks of a specific severity in an analysis run. */
    long countByAnalysisIdAndSeverity(String analysisId, Severity severity);

    /** Delete all risks for an analysis run (cascade on analysis deletion). */
    void deleteByAnalysisId(String analysisId);
}
