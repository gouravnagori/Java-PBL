package com.legal.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Data transfer object encapsulating global and user-level dashboard analytics metrics.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module).
 */
public class DashboardMetricsResponse {

    private long totalDocumentsProcessed;
    private long totalClausesAnalyzed;
    private long highRiskFlagsDetected;
    private double averageRiskScore;
    private Map<String, Long> riskDistribution; // e.g. LOW, MEDIUM, HIGH, CRITICAL
    private Map<String, Long> documentTypeBreakdown; // e.g. NDA, EMPLOYMENT, SERVICE
    private Instant lastUpdated;

    public DashboardMetricsResponse() {
        this.lastUpdated = Instant.now();
    }

    public DashboardMetricsResponse(long totalDocumentsProcessed, long totalClausesAnalyzed,
                                    long highRiskFlagsDetected, double averageRiskScore,
                                    Map<String, Long> riskDistribution,
                                    Map<String, Long> documentTypeBreakdown) {
        this.totalDocumentsProcessed = totalDocumentsProcessed;
        this.totalClausesAnalyzed = totalClausesAnalyzed;
        this.highRiskFlagsDetected = highRiskFlagsDetected;
        this.averageRiskScore = averageRiskScore;
        this.riskDistribution = riskDistribution;
        this.documentTypeBreakdown = documentTypeBreakdown;
        this.lastUpdated = Instant.now();
    }

    public long getTotalDocumentsProcessed() {
        return totalDocumentsProcessed;
    }

    public void setTotalDocumentsProcessed(long totalDocumentsProcessed) {
        this.totalDocumentsProcessed = totalDocumentsProcessed;
    }

    public long getTotalClausesAnalyzed() {
        return totalClausesAnalyzed;
    }

    public void setTotalClausesAnalyzed(long totalClausesAnalyzed) {
        this.totalClausesAnalyzed = totalClausesAnalyzed;
    }

    public long getHighRiskFlagsDetected() {
        return highRiskFlagsDetected;
    }

    public void setHighRiskFlagsDetected(long highRiskFlagsDetected) {
        this.highRiskFlagsDetected = highRiskFlagsDetected;
    }

    public double getAverageRiskScore() {
        return averageRiskScore;
    }

    public void setAverageRiskScore(double averageRiskScore) {
        this.averageRiskScore = averageRiskScore;
    }

    public Map<String, Long> getRiskDistribution() {
        return riskDistribution;
    }

    public void setRiskDistribution(Map<String, Long> riskDistribution) {
        this.riskDistribution = riskDistribution;
    }

    public Map<String, Long> getDocumentTypeBreakdown() {
        return documentTypeBreakdown;
    }

    public void setDocumentTypeBreakdown(Map<String, Long> documentTypeBreakdown) {
        this.documentTypeBreakdown = documentTypeBreakdown;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
