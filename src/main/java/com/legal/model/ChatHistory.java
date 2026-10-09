package com.legal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * MongoDB Entity representing a Q&A / Chat History entry between user and AI Legal Advisor.
 * Owned and managed by Abhishi (AI Legal Advisor / Q&A Module).
 */
@Document(collection = "chat_history")
public class ChatHistory {

    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private String documentId;

    private String question;
    private String answer;
    private String disclaimer;
    private String sourceContext;

    @Indexed
    private LocalDateTime createdAt;

    public ChatHistory() {
        this.createdAt = LocalDateTime.now();
    }

    public ChatHistory(String userId, String documentId, String question, String answer, String disclaimer, String sourceContext) {
        this();
        this.userId = userId;
        this.documentId = documentId;
        this.question = question;
        this.answer = answer;
        this.disclaimer = disclaimer;
        this.sourceContext = sourceContext;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
