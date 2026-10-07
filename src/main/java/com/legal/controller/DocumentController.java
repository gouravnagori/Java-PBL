package com.legal.controller;

import com.legal.dto.*;
import com.legal.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Legal Document Management & Processing.
 * Owned by Gourav Nagori (Document Management & Processing Module).
 */
@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Extracts authenticated user ID from security context (if available).
     */
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return auth.getName();
    }

    /**
     * POST /api/documents/upload
     * Ingests a legal document (.pdf, .docx, .txt, or scanned image),
     * validates, stores, extracts text (PDFBox, POI, or Groq Vision OCR),
     * and persists metadata to MongoDB.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentUploadResponse>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title) {

        String userId = getCurrentUserId();
        log.info("Received document upload request for file: {} (user: {})", file.getOriginalFilename(), userId);

        DocumentUploadResponse response = documentService.uploadAndProcessDocument(file, title, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Document uploaded and processed successfully", response));
    }

    /**
     * GET /api/documents/{id}
     * Retrieves document details, metadata, and lifecycle processing status.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentMetadataResponse>> getDocumentMetadata(@PathVariable String id) {
        String userId = getCurrentUserId();
        DocumentMetadataResponse response = documentService.getDocumentById(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Document retrieved successfully", response));
    }

    /**
     * GET /api/documents/{id}/content
     * Retrieves extracted clean text payload.
     * Core API contract for downstream Legal Analysis and AI Advisor modules.
     */
    @GetMapping("/{id}/content")
    public ResponseEntity<ApiResponse<DocumentContentResponse>> getDocumentContent(@PathVariable String id) {
        String userId = getCurrentUserId();
        DocumentContentResponse response = documentService.getDocumentContent(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Extracted text payload retrieved", response));
    }

    /**
     * GET /api/documents
     * Lists all uploaded documents owned by current user.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DocumentMetadataResponse>>> listDocuments() {
        String userId = getCurrentUserId();
        List<DocumentMetadataResponse> documents = documentService.getUserDocuments(userId);
        return ResponseEntity.ok(ApiResponse.success("Documents listed successfully", documents));
    }

    /**
     * DELETE /api/documents/{id}
     * Deletes document record from MongoDB and cleans up physical file from storage.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable String id) {
        String userId = getCurrentUserId();
        documentService.deleteDocument(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Document deleted successfully", null));
    }

    /**
     * POST /api/documents/ocr
     * Direct OCR transcription endpoint leveraging Groq Vision API for scanned agreements.
     */
    @PostMapping(value = "/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<OcrExtractionResponse>> performOcr(
            @RequestParam("file") MultipartFile file) {
        log.info("Received direct Groq Vision OCR request for: {}", file.getOriginalFilename());
        OcrExtractionResponse response = documentService.performOcr(file);
        return ResponseEntity.ok(ApiResponse.success("OCR transcription completed", response));
    }

    /**
     * GET /api/documents/health
     * Health check endpoint for Document Management Module.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "module", "Document Management & Processing",
                "lead", "Gourav Nagori",
                "status", "UP",
                "supportedFormats", List.of("PDF (PDFBox)", "DOCX (Apache POI)", "TXT", "Image (Groq Vision OCR)"),
                "storageEngine", "MongoDB"
        ));
    }
}
