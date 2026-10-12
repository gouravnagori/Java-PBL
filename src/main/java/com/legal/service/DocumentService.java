package com.legal.service;

import com.legal.dto.DocumentContentResponse;
import com.legal.dto.DocumentMetadataResponse;
import com.legal.dto.DocumentUploadResponse;
import com.legal.dto.OcrExtractionResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Core Service Interface for Document Management & Ingestion.
 * Owned by Gourav Nagori (Document Management & Processing Module).
 */
public interface DocumentService {

    /**
     * Ingest, validate, store, and extract text from an uploaded legal contract.
     * Transitions status from UPLOADED -> PROCESSING -> PROCESSED (or FAILED).
     */
    DocumentUploadResponse uploadAndProcessDocument(MultipartFile file, String title, String userId);

    /**
     * Retrieve full LegalDocument entity by ID.
     */
    com.legal.model.LegalDocument getDocument(String id);

    /**
     * Retrieve metadata and processing status of a document.
     */
    DocumentMetadataResponse getDocumentById(String id, String userId);

    /**
     * Retrieve clean extracted text payload for downstream legal risk analysis and AI advisor.
     */
    DocumentContentResponse getDocumentContent(String id, String userId);

    /**
     * List all documents belonging to a user.
     */
    List<DocumentMetadataResponse> getUserDocuments(String userId);

    /**
     * Delete document from MongoDB and remove physical file from disk.
     */
    void deleteDocument(String id, String userId);

    /**
     * Standalone OCR transcription via Groq Vision API.
     */
    OcrExtractionResponse performOcr(MultipartFile file);
}
