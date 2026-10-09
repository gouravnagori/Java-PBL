package com.legal.dto;

/**
 * Data Transfer Object for incoming user questions to the AI Legal Advisor.
 * Owned by Abhishi (AI Legal Advisor / Q&A Module).
 */
public class AdvisorRequest {

    private String documentId;
    private String question;
    private String documentContext; // Optional document text override if documentId is not in DB

    public AdvisorRequest() {}

    public AdvisorRequest(String documentId, String question) {
        this.documentId = documentId;
        this.question = question;
    }

    public AdvisorRequest(String documentId, String question, String documentContext) {
        this.documentId = documentId;
        this.question = question;
        this.documentContext = documentContext;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDocumentContext() {
        return documentContext;
    }

    public void setDocumentContext(String documentContext) {
        this.documentContext = documentContext;
    }
}
