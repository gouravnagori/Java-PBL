package com.legal.dto;

import com.legal.model.enums.AnalysisStatus;
import com.legal.model.enums.DocType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Complete analysis report DTO returned by GET /api/analysis/{documentId}.
 * Contains the full risk assessment: detected clauses, all risk flags,
 * composite score, and pipeline metadata.
 *
 * Owned by Dilip Kumawat — Legal Analysis & Risk Detection Module.
 */
public class AnalysisReportResponse {

    private String analysisId;
    private String documentId;
    private String userId;

    private DocType docType;
    private AnalysisStatus status;

    /** Normalised 0–100 composite risk score. */
    private double compositeRiskScore;

    /** Plain-language risk posture summary. */
    private String riskSummary;

    /** Total number of clauses segmented from the document. */
    private int totalClausesDetected;

    /** Total number of risk flags raised. */
    private int totalRisksFound;

    /** Count breakdown by severity level. */
    private int criticalCount;
    private int highCount;
    private int mediumCount;
    private int lowCount;

    /** All segmented clauses with their attached risk flags. */
    private List<ClauseDto> clauses;

    /** Only HIGH and CRITICAL risk highlights (for dashboard summary cards). */
    private List<RiskDto> highPriorityRisks;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public AnalysisReportResponse() {}

    // ---- Getters & Setters ----

    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public DocType getDocType() { return docType; }
    public void setDocType(DocType docType) { this.docType = docType; }

    public AnalysisStatus getStatus() { return status; }
    public void setStatus(AnalysisStatus status) { this.status = status; }

    public double getCompositeRiskScore() { return compositeRiskScore; }
    public void setCompositeRiskScore(double compositeRiskScore) { this.compositeRiskScore = compositeRiskScore; }

    public String getRiskSummary() { return riskSummary; }
    public void setRiskSummary(String riskSummary) { this.riskSummary = riskSummary; }

    public int getTotalClausesDetected() { return totalClausesDetected; }
    public void setTotalClausesDetected(int totalClausesDetected) { this.totalClausesDetected = totalClausesDetected; }

    public int getTotalRisksFound() { return totalRisksFound; }
    public void setTotalRisksFound(int totalRisksFound) { this.totalRisksFound = totalRisksFound; }

    public int getCriticalCount() { return criticalCount; }
    public void setCriticalCount(int criticalCount) { this.criticalCount = criticalCount; }

    public int getHighCount() { return highCount; }
    public void setHighCount(int highCount) { this.highCount = highCount; }

    public int getMediumCount() { return mediumCount; }
    public void setMediumCount(int mediumCount) { this.mediumCount = mediumCount; }

    public int getLowCount() { return lowCount; }
    public void setLowCount(int lowCount) { this.lowCount = lowCount; }

    public List<ClauseDto> getClauses() { return clauses; }
    public void setClauses(List<ClauseDto> clauses) { this.clauses = clauses; }

    public List<RiskDto> getHighPriorityRisks() { return highPriorityRisks; }
    public void setHighPriorityRisks(List<RiskDto> highPriorityRisks) { this.highPriorityRisks = highPriorityRisks; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
