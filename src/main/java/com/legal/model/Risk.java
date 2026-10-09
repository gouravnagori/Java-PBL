package com.legal.model;

import com.legal.model.enums.Severity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB Entity representing a single risk flag detected within a contractual clause.
 * Each Risk record provides:
 * - The offending clause text excerpt (citation)
 * - A rule code identifying which detection rule triggered this flag
 * - A severity classification (LOW / MEDIUM / HIGH / CRITICAL)
 * - A plain-language explanation of why this is a risk
 * - An actionable suggestion for the document recipient
 *
 * Owned by Dilip Kumawat — Legal Analysis & Risk Detection Module.
 * F7 / FR9 — Risk Detection, F10 / FR12 — Actionable Suggestions requirements.
 */
@Document(collection = "risks")
public class Risk {

    @Id
    private String id;

    /** Parent Clause this risk was raised from. */
    @Indexed
    private String clauseId;

    /** Parent Analysis run this risk belongs to (denormalised for query efficiency). */
    @Indexed
    private String analysisId;

    /** Owner user ID for multi-tenant isolation. */
    @Indexed
    private String userId;

    /** Short rule code identifying the detection heuristic, e.g. "UNLIMITED_LIABILITY". */
    private String ruleCode;

    /** A short human-readable title for this risk flag. */
    private String riskTitle;

    /** Severity level of this risk. Drives weighted composite score computation. */
    @Indexed
    private Severity severity;

    /** The specific text excerpt from the clause that triggered this flag. */
    private String citedText;

    /**
     * Plain-language explanation of why this clause text is considered risky,
     * written at a level understandable to a non-lawyer.
     */
    private String explanation;

    /**
     * Actionable recommendation: what the document recipient should negotiate,
     * request, or be aware of before signing.
     */
    private String suggestion;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Risk() {}

    public Risk(String clauseId, String analysisId, String userId,
                String ruleCode, String riskTitle, Severity severity,
                String citedText, String explanation, String suggestion) {
        this.clauseId = clauseId;
        this.analysisId = analysisId;
        this.userId = userId;
        this.ruleCode = ruleCode;
        this.riskTitle = riskTitle;
        this.severity = severity;
        this.citedText = citedText;
        this.explanation = explanation;
        this.suggestion = suggestion;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClauseId() { return clauseId; }
    public void setClauseId(String clauseId) { this.clauseId = clauseId; }

    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRuleCode() { return ruleCode; }
    public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }

    public String getRiskTitle() { return riskTitle; }
    public void setRiskTitle(String riskTitle) { this.riskTitle = riskTitle; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public String getCitedText() { return citedText; }
    public void setCitedText(String citedText) { this.citedText = citedText; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
}
