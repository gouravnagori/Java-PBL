package com.legal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * MongoDB document entity representing a compiled Legal Audit Report.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module).
 */
@Document(collection = "legal_reports")
public class LegalReport {

    @Id
    private String id;
    private String documentId;
    private String userId;
    private String documentTitle;
    private String documentType;
    private double overallRiskScore;
    private String riskCategory; // LOW, MEDIUM, HIGH, CRITICAL
    private String executiveSummary;
    private List<String> keyRiskFindings;
    private Map<String, Integer> severityBreakdown;
    private List<String> recommendedNextSteps;
    private String pdfStoragePath;
    private String disclaimerNotice;
    private Instant createdAt;
    private Instant updatedAt;

    public LegalReport() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public LegalReport(String documentId, String userId, String documentTitle, String documentType,
                       double overallRiskScore, String riskCategory, String executiveSummary,
                       List<String> keyRiskFindings, Map<String, Integer> severityBreakdown,
                       List<String> recommendedNextSteps, String pdfStoragePath, String disclaimerNotice) {
        this.documentId = documentId;
        this.userId = userId;
        this.documentTitle = documentTitle;
        this.documentType = documentType;
        this.overallRiskScore = overallRiskScore;
        this.riskCategory = riskCategory;
        this.executiveSummary = executiveSummary;
        this.keyRiskFindings = keyRiskFindings;
        this.severityBreakdown = severityBreakdown;
        this.recommendedNextSteps = recommendedNextSteps;
        this.pdfStoragePath = pdfStoragePath;
        this.disclaimerNotice = disclaimerNotice;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPdfStoragePath() {
        return pdfStoragePath;
    }

    public void setPdfStoragePath(String pdfStoragePath) {
        this.pdfStoragePath = pdfStoragePath;
    }

    public String getDisclaimerNotice() {
        return disclaimerNotice;
    }

    public void setDisclaimerNotice(String disclaimerNotice) {
        this.disclaimerNotice = disclaimerNotice;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
