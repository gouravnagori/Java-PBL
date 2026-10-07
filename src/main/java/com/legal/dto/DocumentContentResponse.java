package com.legal.dto;

import com.legal.model.DocumentStatus;

/**
 * DTO exposing raw extracted text and metrics to the downstream Legal Analysis engine.
 */
public class DocumentContentResponse {

    private String documentId;
    private String title;
    private String originalFileName;
    private DocumentStatus status;
    private int wordCount;
    private int characterCount;
    private String extractedText;

    public DocumentContentResponse() {}

    public DocumentContentResponse(String documentId, String title, String originalFileName,
                                   DocumentStatus status, int wordCount, int characterCount, String extractedText) {
        this.documentId = documentId;
        this.title = title;
        this.originalFileName = originalFileName;
        this.status = status;
        this.wordCount = wordCount;
        this.characterCount = characterCount;
        this.extractedText = extractedText;
    }

    // Getters and Setters
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public DocumentStatus getStatus() { return status; }
    public void setStatus(DocumentStatus status) { this.status = status; }

    public int getWordCount() { return wordCount; }
    public void setWordCount(int wordCount) { this.wordCount = wordCount; }

    public int getCharacterCount() { return characterCount; }
    public void setCharacterCount(int characterCount) { this.characterCount = characterCount; }

    public String getExtractedText() { return extractedText; }
    public void setExtractedText(String extractedText) { this.extractedText = extractedText; }
}
