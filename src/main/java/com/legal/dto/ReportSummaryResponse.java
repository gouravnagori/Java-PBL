package com.legal.dto;

import java.time.Instant;
import java.util.List;

/**
 * Data transfer object encapsulating legal report summary and metadata.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module).
 */
public class ReportSummaryResponse {

    private String reportId;
    private String documentId;
    private String fileName;
    private String documentType;
    private double riskScore;
    private String riskLevel;
    private int totalClauses;
    private int flaggedRisksCount;
    private String downloadPdfUrl;
    private Instant generatedAt;
    private String userId;

    public ReportSummaryResponse() {}

    public ReportSummaryResponse(String reportId, String documentId, String fileName, String documentType,
                                 double riskScore, String riskLevel, int totalClauses, int flaggedRisksCount,
                                 String downloadPdfUrl, Instant generatedAt) {
        this(reportId, documentId, fileName, documentType, riskScore, riskLevel, totalClauses, flaggedRisksCount, downloadPdfUrl, generatedAt, null);
    }

    public ReportSummaryResponse(String reportId, String documentId, String fileName, String documentType,
                                 double riskScore, String riskLevel, int totalClauses, int flaggedRisksCount,
                                 String downloadPdfUrl, Instant generatedAt, String userId) {
        this.reportId = reportId;
        this.documentId = documentId;
        this.fileName = fileName;
        this.documentType = documentType;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.totalClauses = totalClauses;
        this.flaggedRisksCount = flaggedRisksCount;
        this.downloadPdfUrl = downloadPdfUrl;
        this.generatedAt = generatedAt;
        this.userId = userId;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(double riskScore) {
        this.riskScore = riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public int getTotalClauses() {
        return totalClauses;
    }

    public void setTotalClauses(int totalClauses) {
        this.totalClauses = totalClauses;
    }

    public int getFlaggedRisksCount() {
        return flaggedRisksCount;
    }

    public void setFlaggedRisksCount(int flaggedRisksCount) {
        this.flaggedRisksCount = flaggedRisksCount;
    }

    public String getDownloadPdfUrl() {
        return downloadPdfUrl;
    }

    public void setDownloadPdfUrl(String downloadPdfUrl) {
        this.downloadPdfUrl = downloadPdfUrl;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
