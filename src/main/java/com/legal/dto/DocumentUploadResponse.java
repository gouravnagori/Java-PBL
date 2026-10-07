package com.legal.dto;

import com.legal.model.DocumentMetadata;
import com.legal.model.DocumentStatus;

import java.time.LocalDateTime;

/**
 * DTO returned immediately upon document ingestion.
 */
public class DocumentUploadResponse {

    private String documentId;
    private String title;
    private String originalFileName;
    private DocumentStatus status;
    private String statusMessage;
    private DocumentMetadata metadata;
    private String textPreview; // First 300 characters of extracted text
    private LocalDateTime uploadedAt;

    public DocumentUploadResponse() {}

    // Getters and Setters
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public DocumentStatus getStatus() { return status; }
    public void setStatus(DocumentStatus status) { this.status = status; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    public DocumentMetadata getMetadata() { return metadata; }
    public void setMetadata(DocumentMetadata metadata) { this.metadata = metadata; }

    public String getTextPreview() { return textPreview; }
    public void setTextPreview(String textPreview) { this.textPreview = textPreview; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
