package com.legal.dto;

import java.util.List;

/**
 * DTO representing a single segmented clause with its attached risk flags.
 * Owned by Dilip Kumawat — Legal Analysis & Risk Detection Module.
 */
public class ClauseDto {

    private String id;
    private String clauseType;
    private String rawText;
    private String pageReference;
    private int clauseIndex;
    private boolean hasRisk;
    private List<RiskDto> risks;

    public ClauseDto() {}

    // ---- Getters & Setters ----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    public List<RiskDto> getRisks() { return risks; }
    public void setRisks(List<RiskDto> risks) { this.risks = risks; }
}
