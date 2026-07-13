package com.legal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

/**
 * MongoDB document entity storing pre-aggregated analytics statistics.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module).
 */
@Document(collection = "analytics_summaries")
public class AnalyticsSummary {

    @Id
    private String id;
    private String userId; // Optional: null for system-wide metrics
    private long totalDocuments;
    private long totalClauses;
    private long totalHighRiskFlags;
    private double averageRiskScore;
    private Map<String, Long> riskSeverityDistribution;
    private Map<String, Long> documentCategoryDistribution;
    private Instant calculatedAt;

    public AnalyticsSummary() {
        this.calculatedAt = Instant.now();
    }

    public AnalyticsSummary(String userId, long totalDocuments, long totalClauses,
                            long totalHighRiskFlags, double averageRiskScore,
                            Map<String, Long> riskSeverityDistribution,
                            Map<String, Long> documentCategoryDistribution) {
        this.userId = userId;
        this.totalDocuments = totalDocuments;
        this.totalClauses = totalClauses;
        this.totalHighRiskFlags = totalHighRiskFlags;
        this.averageRiskScore = averageRiskScore;
        this.riskSeverityDistribution = riskSeverityDistribution;
        this.documentCategoryDistribution = documentCategoryDistribution;
        this.calculatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTotalDocuments() {
        return totalDocuments;
    }

    public void setTotalDocuments(long totalDocuments) {
        this.totalDocuments = totalDocuments;
    }

    public long getTotalClauses() {
        return totalClauses;
    }

    public void setTotalClauses(long totalClauses) {
        this.totalClauses = totalClauses;
    }

    public long getTotalHighRiskFlags() {
        return totalHighRiskFlags;
    }

    public void setTotalHighRiskFlags(long totalHighRiskFlags) {
        this.totalHighRiskFlags = totalHighRiskFlags;
    }

    public double getAverageRiskScore() {
        return averageRiskScore;
    }

    public void setAverageRiskScore(double averageRiskScore) {
        this.averageRiskScore = averageRiskScore;
    }

    public Map<String, Long> getRiskSeverityDistribution() {
        return riskSeverityDistribution;
    }

    public void setRiskSeverityDistribution(Map<String, Long> riskSeverityDistribution) {
        this.riskSeverityDistribution = riskSeverityDistribution;
    }

    public Map<String, Long> getDocumentCategoryDistribution() {
        return documentCategoryDistribution;
    }

    public void setDocumentCategoryDistribution(Map<String, Long> documentCategoryDistribution) {
        this.documentCategoryDistribution = documentCategoryDistribution;
    }

    public Instant getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(Instant calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
}
