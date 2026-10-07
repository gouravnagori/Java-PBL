package com.legal.dto;

/**
 * Request payload for standalone base64 OCR extraction via Groq Vision API.
 */
public class OcrExtractionRequest {

    private String base64Image;
    private String mimeType; // e.g., image/jpeg, image/png
    private String documentHint; // Optional hint (e.g., "Rental Agreement", "Court Notice")

    public OcrExtractionRequest() {}

    public OcrExtractionRequest(String base64Image, String mimeType) {
        this.base64Image = base64Image;
        this.mimeType = mimeType;
    }

    public String getBase64Image() { return base64Image; }
    public void setBase64Image(String base64Image) { this.base64Image = base64Image; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public String getDocumentHint() { return documentHint; }
    public void setDocumentHint(String documentHint) { this.documentHint = documentHint; }
}
