package com.legal.dto;

import java.time.LocalDateTime;

/**
 * Result of Groq Vision OCR transcription.
 */
public class OcrExtractionResponse {

    private boolean success;
    private String transcribedText;
    private String detectedDocumentType;
    private int wordCount;
    private int characterCount;
    private String modelUsed;
    private long executionTimeMs;
    private String errorMessage;
    private LocalDateTime timestamp;

    public OcrExtractionResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public static OcrExtractionResponse success(String transcribedText, String detectedType,
                                               String modelUsed, long executionTimeMs) {
        OcrExtractionResponse resp = new OcrExtractionResponse();
        resp.setSuccess(true);
        resp.setTranscribedText(transcribedText);
        resp.setDetectedDocumentType(detectedType);
        resp.setWordCount(transcribedText != null ? transcribedText.trim().split("\\s+").length : 0);
        resp.setCharacterCount(transcribedText != null ? transcribedText.length() : 0);
        resp.setModelUsed(modelUsed);
        resp.setExecutionTimeMs(executionTimeMs);
        return resp;
    }

    public static OcrExtractionResponse failure(String errorMessage) {
        OcrExtractionResponse resp = new OcrExtractionResponse();
        resp.setSuccess(false);
        resp.setErrorMessage(errorMessage);
        return resp;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getTranscribedText() { return transcribedText; }
    public void setTranscribedText(String transcribedText) { this.transcribedText = transcribedText; }

    public String getDetectedDocumentType() { return detectedDocumentType; }
    public void setDetectedDocumentType(String detectedDocumentType) { this.detectedDocumentType = detectedDocumentType; }

    public int getWordCount() { return wordCount; }
    public void setWordCount(int wordCount) { this.wordCount = wordCount; }

    public int getCharacterCount() { return characterCount; }
    public void setCharacterCount(int characterCount) { this.characterCount = characterCount; }

    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }

    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
