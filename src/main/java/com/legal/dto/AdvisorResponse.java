package com.legal.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for responses returned by the AI Legal Advisor.
 * Includes answer, plain-language recommendations, source context, legal disclaimer, and timestamp.
 * Owned by Abhishi (AI Legal Advisor / Q&A Module).
 */
public class AdvisorResponse {

    private String id;
    private String documentId;
    private String question;
    private String answer;
    private String disclaimer;
    private String sourceContext;
    private LocalDateTime timestamp;

    public static final String DEFAULT_DISCLAIMER =
            "Disclaimer: This AI Legal Advisor provides document-aware informational guidance for reference only. " +
            "It does not constitute formal legal advice, legal representation, or an attorney-client relationship. " +
            "For binding legal decisions, please consult a qualified advocate or attorney.";

    public AdvisorResponse() {
        this.timestamp = LocalDateTime.now();
        this.disclaimer = DEFAULT_DISCLAIMER;
    }

    public AdvisorResponse(String id, String documentId, String question, String answer, String sourceContext) {
        this();
        this.id = id;
        this.documentId = documentId;
        this.question = question;
        this.answer = answer;
        this.sourceContext = sourceContext;
    }

    // Getters and Setters
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public String getSourceContext() {
        return sourceContext;
    }

    public void setSourceContext(String sourceContext) {
        this.sourceContext = sourceContext;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
