package com.legal.model;

import java.time.LocalDateTime;

/**
 * Embedded document metadata describing technical metrics,
 * hash integrity, and extraction properties.
 */
public class DocumentMetadata {

    private String fileName;
    private String contentType;
    private long fileSize;
    private String fileHash; // SHA-256 Checksum for tamper-proofing & deduplication
    private int pageCount;
    private int wordCount;
    private int characterCount;
    private String extractionMethod; // e.g., PDFBOX, POI_DOCX, TEXT_PLAIN, GROQ_VISION_OCR
    private long extractionTimeMs;
    private LocalDateTime extractedAt;

    public DocumentMetadata() {
        this.extractedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getFileType() {
        if (contentType != null && !contentType.isBlank()) {
            if (contentType.contains("pdf")) return "PDF";
            if (contentType.contains("wordprocessingml") || contentType.contains("docx")) return "DOCX";
            if (contentType.contains("plain")) return "TXT";
            if (contentType.contains("image")) return "IMAGE";
            return contentType;
        }
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf('.') + 1).toUpperCase();
        }
        return "CONTRACT";
    }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }

    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }

    public int getWordCount() { return wordCount; }
    public void setWordCount(int wordCount) { this.wordCount = wordCount; }

    public int getCharacterCount() { return characterCount; }
    public void setCharacterCount(int characterCount) { this.characterCount = characterCount; }

    public String getExtractionMethod() { return extractionMethod; }
    public void setExtractionMethod(String extractionMethod) { this.extractionMethod = extractionMethod; }

    public long getExtractionTimeMs() { return extractionTimeMs; }
    public void setExtractionTimeMs(long extractionTimeMs) { this.extractionTimeMs = extractionTimeMs; }

    public LocalDateTime getExtractedAt() { return extractedAt; }
    public void setExtractedAt(LocalDateTime extractedAt) { this.extractedAt = extractedAt; }
}
