package com.legal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * MongoDB Entity representing a Legal Document within the system.
 * Managed by Gourav Nagori (Document Management & Processing Module).
 */
@Document(collection = "documents")
public class LegalDocument {

    @Id
    private String id;

    @Indexed
    private String userId; // Owner user ID for multi-tenant isolation

    private String title;
    private String originalFileName;
    private String storagePath;

    @Indexed
    private DocumentStatus status; // UPLOADED, PROCESSING, PROCESSED, FAILED

    private String statusMessage; // Additional diagnostic message if FAILED or info

    private DocumentMetadata metadata;

    private String extractedText;

    @Indexed
    private LocalDateTime uploadedAt;

    private LocalDateTime updatedAt;

    public LegalDocument() {
        this.status = DocumentStatus.UPLOADED;
        this.uploadedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public LegalDocument(String title, String originalFileName, String userId) {
        this();
        this.title = title;
        this.originalFileName = originalFileName;
        this.userId = userId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }

    public DocumentStatus getStatus() { return status; }
    public void setStatus(DocumentStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    public DocumentMetadata getMetadata() { return metadata; }
    public void setMetadata(DocumentMetadata metadata) { this.metadata = metadata; }

    public String getExtractedText() { return extractedText; }
    public void setExtractedText(String extractedText) { this.extractedText = extractedText; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
