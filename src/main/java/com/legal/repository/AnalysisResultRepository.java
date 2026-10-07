package com.legal.repository;

import com.legal.model.Analysis;
import com.legal.model.enums.AnalysisStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB Repository for the Analysis entity.
 * Owned by Dilip Kumawat — Legal Analysis & Risk Detection Module.
 */
@Repository
public interface AnalysisResultRepository extends MongoRepository<Analysis, String> {

    /** Find the analysis for a specific document owned by a specific user. */
    Optional<Analysis> findByDocumentIdAndUserId(String documentId, String userId);

    /** Find analysis by ID scoped to a specific user (ownership check). */
    Optional<Analysis> findByIdAndUserId(String id, String userId);

    /** List all analysis results for a user ordered by most recent first. */
    List<Analysis> findByUserIdOrderByStartedAtDesc(String userId);

    /** Find all analyses in a given lifecycle status. */
    List<Analysis> findByStatus(AnalysisStatus status);

    /** Check whether an analysis already exists for a document. */
    boolean existsByDocumentIdAndUserId(String documentId, String userId);

    /** Delete a specific analysis scoped to user ownership. */
    void deleteByIdAndUserId(String id, String userId);
}
