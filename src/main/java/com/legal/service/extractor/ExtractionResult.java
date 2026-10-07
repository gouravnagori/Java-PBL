package com.legal.service.extractor;

/**
 * Encapsulates the textual content and metrics produced by a document extractor.
 */
public class ExtractionResult {

    private String text;
    private int pageCount;
    private int wordCount;
    private int characterCount;
    private String extractionMethod;
    private boolean isScannedOrEmpty;

    public ExtractionResult() {}

    public ExtractionResult(String text, int pageCount, String extractionMethod) {
        this.text = text != null ? text.trim() : "";
        this.pageCount = pageCount;
        this.extractionMethod = extractionMethod;
        this.characterCount = this.text.length();
        this.wordCount = this.text.isEmpty() ? 0 : this.text.split("\\s+").length;
        this.isScannedOrEmpty = this.text.isEmpty();
    }

    // Getters and Setters
    public String getText() { return text; }
    public void setText(String text) {
        this.text = text;
        this.characterCount = text != null ? text.length() : 0;
        this.wordCount = (text != null && !text.isBlank()) ? text.trim().split("\\s+").length : 0;
    }

    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }

    public int getWordCount() { return wordCount; }
    public void setWordCount(int wordCount) { this.wordCount = wordCount; }

    public int getCharacterCount() { return characterCount; }
    public void setCharacterCount(int characterCount) { this.characterCount = characterCount; }

    public String getExtractionMethod() { return extractionMethod; }
    public void setExtractionMethod(String extractionMethod) { this.extractionMethod = extractionMethod; }

    public boolean isScannedOrEmpty() { return isScannedOrEmpty; }
    public void setScannedOrEmpty(boolean scannedOrEmpty) { isScannedOrEmpty = scannedOrEmpty; }
}
