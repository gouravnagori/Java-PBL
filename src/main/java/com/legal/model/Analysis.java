package com.legal.model;

import com.legal.model.enums.AnalysisStatus;
import com.legal.model.enums.DocType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * MongoDB Entity representing the result of Dilip's Legal Analysis & Risk Detection Engine
 * run against a single uploaded legal document.
 *
 * Each Analysis record encapsulates:
 * - The detected document category (DocType)
 * - A normalised 0–100 composite risk score
 * - Lifecycle status (PENDING → ANALYSING → COMPLETED / FAILED)
 * - A human-readable risk summary with clause citations
 *
 * Owned by Dilip Kumawat — Legal Analysis & Risk Detection Module.
 */
@Document(collection = "analyses")
@CompoundIndex(name = "user_doc_idx", def = "{'userId': 1, 'documentId': 1}", unique = true)
public class Analysis {

    @Id
    private String id;

    /** Owner of the document — for multi-tenant data isolation. */
    @Indexed
    private String userId;

    /** Reference to the LegalDocument in the 'documents' collection (Gourav's module). */
    @Indexed
    private String documentId;

    /** Detected document category (NDA, EMPLOYMENT, RENTAL, etc.). */
    private DocType docType;

    /** Pipeline lifecycle state. */
    @Indexed
    private AnalysisStatus status;

    /** Optional error message when status = FAILED. */
    private String errorMessage;

    /**
     * Normalised composite risk score in range [0, 100].
     * 0 = no detected risk; 100 = maximum risk density.
     */
    private double compositeRiskScore;

    /** Number of clauses segmented from the document text. */
    private int totalClausesDetected;

    /** Count of individual risk flags raised across all clauses. */
    private int totalRisksFound;

    /** Plain-language executive summary of the overall document risk posture. */
    private String riskSummary;

    /** Timestamp when this analysis run was initiated. */
    @Indexed
    private LocalDateTime startedAt;

    /** Timestamp when this analysis run completed (or failed). */
    private LocalDateTime completedAt;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Analysis() {
        this.status = AnalysisStatus.PENDING;
        this.startedAt = LocalDateTime.now();
        this.compositeRiskScore = 0.0;
    }

    public Analysis(String documentId, String userId) {
        this();
        this.documentId = documentId;
        this.userId = userId;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public DocType getDocType() { return docType; }
    public void setDocType(DocType docType) { this.docType = docType; }

    public AnalysisStatus getStatus() { return status; }
    public void setStatus(AnalysisStatus status) { this.status = status; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public double getCompositeRiskScore() { return compositeRiskScore; }
    public void setCompositeRiskScore(double compositeRiskScore) { this.compositeRiskScore = compositeRiskScore; }

    public int getTotalClausesDetected() { return totalClausesDetected; }
    public void setTotalClausesDetected(int totalClausesDetected) { this.totalClausesDetected = totalClausesDetected; }

    public int getTotalRisksFound() { return totalRisksFound; }
    public void setTotalRisksFound(int totalRisksFound) { this.totalRisksFound = totalRisksFound; }

    public String getRiskSummary() { return riskSummary; }
    public void setRiskSummary(String riskSummary) { this.riskSummary = riskSummary; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
