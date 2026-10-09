package com.legal.dto;

import com.legal.model.enums.Severity;

/**
 * DTO representing a single risk flag in an API response.
 * Owned by Dilip Kumawat — Legal Analysis & Risk Detection Module.
 */
public class RiskDto {

    private String id;
    private String clauseId;
    private String ruleCode;
    private String riskTitle;
    private Severity severity;
    private String citedText;
    private String explanation;
    private String suggestion;

    public RiskDto() {}

    // ---- Getters & Setters ----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClauseId() { return clauseId; }
    public void setClauseId(String clauseId) { this.clauseId = clauseId; }

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
