package com.legal.repository;

import com.legal.model.AnalyticsSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data MongoRepository interface for pre-aggregated AnalyticsSummary entities.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module).
 */
@Repository
public interface AnalyticsRepository extends MongoRepository<AnalyticsSummary, String> {

    Optional<AnalyticsSummary> findTopByUserIdOrderByCalculatedAtDesc(String userId);

    Optional<AnalyticsSummary> findTopByUserIdIsNullOrderByCalculatedAtDesc();
}
