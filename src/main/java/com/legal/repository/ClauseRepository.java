package com.legal.repository;

import com.legal.model.Clause;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MongoDB Repository for the Clause entity.
 * Owned by Dilip Kumawat — Legal Analysis & Risk Detection Module.
 */
@Repository
public interface ClauseRepository extends MongoRepository<Clause, String> {

    /** Retrieve all clauses for a given analysis run, ordered by position. */
    List<Clause> findByAnalysisIdOrderByClauseIndexAsc(String analysisId);

    /** Retrieve only risk-flagged clauses for an analysis run. */
    List<Clause> findByAnalysisIdAndHasRiskTrue(String analysisId);

    /** Count total clauses detected in an analysis run. */
    long countByAnalysisId(String analysisId);

    /** Delete all clauses belonging to an analysis run (cascade on analysis deletion). */
    void deleteByAnalysisId(String analysisId);
}
