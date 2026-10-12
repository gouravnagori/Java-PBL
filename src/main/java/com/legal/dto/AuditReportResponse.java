package com.legal.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Complete Legal Audit Report response payload.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module).
 */
public class AuditReportResponse {

    private String reportId;
    private String documentId;
    private String documentTitle;
    private String documentType;
    private double overallRiskScore;
    private String riskCategory; // LOW, MEDIUM, HIGH, CRITICAL
    private String executiveSummary;
    private List<String> keyRiskFindings;
    private Map<String, Integer> severityBreakdown;
    private List<String> recommendedNextSteps;
    private String disclaimerNotice;
    private Instant generatedAt;

    public AuditReportResponse() {}

    public AuditReportResponse(String reportId, String documentId, String documentTitle, String documentType,
                               double overallRiskScore, String riskCategory, String executiveSummary,
                               List<String> keyRiskFindings, Map<String, Integer> severityBreakdown,
                               List<String> recommendedNextSteps, String disclaimerNotice, Instant generatedAt) {
        this.reportId = reportId;
        this.documentId = documentId;
        this.documentTitle = documentTitle;
        this.documentType = documentType;
        this.overallRiskScore = overallRiskScore;
        this.riskCategory = riskCategory;
        this.executiveSummary = executiveSummary;
        this.keyRiskFindings = keyRiskFindings;
        this.severityBreakdown = severityBreakdown;
        this.recommendedNextSteps = recommendedNextSteps;
        this.disclaimerNotice = disclaimerNotice;
        this.generatedAt = generatedAt;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public double getOverallRiskScore() {
        return overallRiskScore;
    }

    public void setOverallRiskScore(double overallRiskScore) {
        this.overallRiskScore = overallRiskScore;
    }

    public String getRiskCategory() {
        return riskCategory;
    }

    public void setRiskCategory(String riskCategory) {
        this.riskCategory = riskCategory;
    }

    public String getExecutiveSummary() {
        return executiveSummary;
    }

    public void setExecutiveSummary(String executiveSummary) {
        this.executiveSummary = executiveSummary;
    }

    public List<String> getKeyRiskFindings() {
        return keyRiskFindings;
    }

    public void setKeyRiskFindings(List<String> keyRiskFindings) {
        this.keyRiskFindings = keyRiskFindings;
    }

    public Map<String, Integer> getSeverityBreakdown() {
        return severityBreakdown;
    }

    public void setSeverityBreakdown(Map<String, Integer> severityBreakdown) {
        this.severityBreakdown = severityBreakdown;
    }

    public List<String> getRecommendedNextSteps() {
        return recommendedNextSteps;
    }

    public void setRecommendedNextSteps(List<String> recommendedNextSteps) {
        this.recommendedNextSteps = recommendedNextSteps;
    }

    public String getDisclaimerNotice() {
        return disclaimerNotice;
    }

    public void setDisclaimerNotice(String disclaimerNotice) {
        this.disclaimerNotice = disclaimerNotice;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }
}
