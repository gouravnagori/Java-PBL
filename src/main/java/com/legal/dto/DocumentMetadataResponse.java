package com.legal.dto;

import com.legal.model.DocumentMetadata;
import com.legal.model.DocumentStatus;

import java.time.LocalDateTime;

/**
 * Detailed DTO describing document metadata and current state.
 */
public class DocumentMetadataResponse {

    private String id;
    private String userId;
    private String title;
    private String originalFileName;
    private DocumentStatus status;
    private String statusMessage;
    private DocumentMetadata metadata;
    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;

    public DocumentMetadataResponse() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

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

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
