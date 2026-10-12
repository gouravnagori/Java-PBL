package com.legal.dto;

import com.legal.model.CaseCategory;
import com.legal.model.CaseStatus;
import com.legal.model.LegalCase;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class CaseResponse {

    private Long id;
    private String caseNumber;
    private String title;
    private String description;
    private String clientName;
    private CaseCategory category;
    private CaseStatus status;
    private Long userId;
    private String userName;
    private int documentCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public CaseResponse(LegalCase legalCase) {
        this.id = legalCase.getId();
        this.caseNumber = legalCase.getCaseNumber();
        this.title = legalCase.getTitle();
        this.description = legalCase.getDescription();
        this.clientName = legalCase.getClientName();
        this.category = legalCase.getCategory();
        this.status = legalCase.getStatus();
        this.userId = legalCase.getUser().getId();
        this.userName = legalCase.getUser().getName();
        this.documentCount = legalCase.getDocumentCount();
        this.createdAt = legalCase.getCreatedAt();
        this.updatedAt = legalCase.getUpdatedAt();
    }

    public Long getId() { return id; }
    public String getCaseNumber() { return caseNumber; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getClientName() { return clientName; }
    public CaseCategory getCategory() { return category; }
    public CaseStatus getStatus() { return status; }
    public Long getUserId() { return userId; }
    public String getUserName() { return userName; }
    public int getDocumentCount() { return documentCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
