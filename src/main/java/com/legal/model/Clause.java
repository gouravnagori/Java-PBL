package com.legal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB Entity representing a single contractual clause segmented from a legal document.
 * Clauses are the atomic units processed by the Rule-Based Risk Scoring Engine.
 *
 * Owned by Dilip Kumawat — Legal Analysis & Risk Detection Module.
 * F6 / FR8 — Clause Extraction requirement.
 */
@Document(collection = "clauses")
public class Clause {

    @Id
    private String id;

    /** Parent Analysis run this clause was extracted from. */
    @Indexed
    private String analysisId;

    /** Owner user ID for multi-tenant isolation. */
    @Indexed
    private String userId;

    /**
     * Clause type label assigned by ClassificationService.
     * Examples: "CONFIDENTIALITY", "INDEMNIFICATION", "TERMINATION",
     *           "NON_COMPETE", "GOVERNING_LAW", "PAYMENT_TERMS", "AUTO_RENEWAL",
     *           "LIABILITY_LIMITATION", "DISPUTE_RESOLUTION", "GENERAL"
     */
    private String clauseType;

    /** The raw text segment extracted from the document. */
    private String rawText;

    /** Approximate page or section reference within the source document. */
    private String pageReference;

    /** Sequential position of this clause in the segmented document (1-indexed). */
    private int clauseIndex;

    /** Whether at least one Risk flag was raised for this clause. */
    private boolean hasRisk;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Clause() {}

    public Clause(String analysisId, String userId, String clauseType, String rawText,
                  int clauseIndex) {
        this.analysisId = analysisId;
        this.userId = userId;
        this.clauseType = clauseType;
        this.rawText = rawText;
        this.clauseIndex = clauseIndex;
        this.hasRisk = false;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getClauseType() { return clauseType; }
    public void setClauseType(String clauseType) { this.clauseType = clauseType; }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }

    public String getPageReference() { return pageReference; }
    public void setPageReference(String pageReference) { this.pageReference = pageReference; }

    public int getClauseIndex() { return clauseIndex; }
    public void setClauseIndex(int clauseIndex) { this.clauseIndex = clauseIndex; }

    public boolean isHasRisk() { return hasRisk; }
    public void setHasRisk(boolean hasRisk) { this.hasRisk = hasRisk; }
}
