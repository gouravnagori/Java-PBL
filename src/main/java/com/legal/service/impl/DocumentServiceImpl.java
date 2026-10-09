package com.legal.service.impl;

import com.legal.dto.DocumentContentResponse;
import com.legal.dto.DocumentMetadataResponse;
import com.legal.dto.DocumentUploadResponse;
import com.legal.dto.OcrExtractionResponse;
import com.legal.exception.DocumentNotFoundException;
import com.legal.exception.FileStorageException;
import com.legal.exception.InvalidFileException;
import com.legal.model.DocumentMetadata;
import com.legal.model.DocumentStatus;
import com.legal.model.LegalDocument;
import com.legal.repository.DocumentRepository;
import com.legal.service.DocumentService;
import com.legal.service.extractor.DocumentExtractor;
import com.legal.service.extractor.ExtractionResult;
import com.legal.service.ocr.GroqVisionOcrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Enterprise implementation of DocumentService.
 * Handles validation, local file storage, hashing, extraction routing (PDFBox, POI, Groq Vision OCR),
 * metadata calculation, and MongoDB persistence.
 * Module: Document Management & Processing (Gourav Nagori)
 */
@Service
public class DocumentServiceImpl implements DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024; // 15 MB

    private final DocumentRepository documentRepository;
    private final List<DocumentExtractor> extractors;
    private final GroqVisionOcrService groqVisionOcrService;
    private final Path storageLocation;

    public DocumentServiceImpl(
            DocumentRepository documentRepository,
            List<DocumentExtractor> extractors,
            GroqVisionOcrService groqVisionOcrService,
            @Value("${app.storage.upload-dir:uploads/documents}") String uploadDir) {
        this.documentRepository = documentRepository;
        this.extractors = extractors;
        this.groqVisionOcrService = groqVisionOcrService;
        this.storageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.storageLocation);
            log.info("Initialized document storage directory at: {}", this.storageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not initialize storage directory at " + uploadDir, ex);
        }
    }

    @Override
    public DocumentUploadResponse uploadAndProcessDocument(MultipartFile file, String title, String userId) {
        long startTime = System.currentTimeMillis();

        // 1. Validation
        validateFile(file);

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        long fileSize = file.getSize();

        log.info("Processing upload: {} ({} bytes, type: {}) for user: {}", originalFilename, fileSize, userId);

        // 2. Compute SHA-256 Checksum for tamper-evidence and deduplication
        String fileHash = computeSha256(file);

        // 3. Store file safely to physical disk
        String storedFilename = UUID.randomUUID() + "_" + originalFilename;
        Path targetPath = this.storageLocation.resolve(storedFilename);
        try (InputStream is = file.getInputStream()) {
            Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file " + originalFilename, e);
        }

        // 4. Create document entity in PROCESSING state
        LegalDocument document = new LegalDocument();
        document.setUserId(userId);
        document.setTitle((title != null && !title.isBlank()) ? title : originalFilename);
        document.setOriginalFileName(originalFilename);
        document.setStoragePath(targetPath.toString());
        document.setStatus(DocumentStatus.PROCESSING);
        document = documentRepository.save(document);

        // 5. Route to appropriate extraction engine
        ExtractionResult extractionResult;
        try {
            if (isImageFile(contentType, originalFilename)) {
                // Route image to Groq Vision OCR
                log.info("Routing image to Groq Vision OCR engine: {}", originalFilename);
                OcrExtractionResponse ocrResp = groqVisionOcrService.extractTextFromImage(file);
                extractionResult = new ExtractionResult(
                        ocrResp.getTranscribedText(),
                        1,
                        "GROQ_VISION_OCR"
                );
            } else {
                // Route to appropriate document extractor (PDFBox, POI DOCX, Text)
                DocumentExtractor extractor = findExtractor(contentType, originalFilename);
                try (InputStream stream = Files.newInputStream(targetPath)) {
                    extractionResult = extractor.extract(stream, originalFilename);
                }
            }

            long duration = System.currentTimeMillis() - startTime;

            // 6. Assemble Document Metadata
            DocumentMetadata metadata = new DocumentMetadata();
            metadata.setFileName(originalFilename);
            metadata.setContentType(contentType);
            metadata.setFileSize(fileSize);
            metadata.setFileHash(fileHash);
            metadata.setPageCount(extractionResult.getPageCount());
            metadata.setWordCount(extractionResult.getWordCount());
            metadata.setCharacterCount(extractionResult.getCharacterCount());
            metadata.setExtractionMethod(extractionResult.getExtractionMethod());
            metadata.setExtractionTimeMs(duration);
            metadata.setExtractedAt(LocalDateTime.now());

            // 7. Update document state to PROCESSED
            document.setMetadata(metadata);
            document.setExtractedText(extractionResult.getText());
            document.setStatus(DocumentStatus.PROCESSED);
            document.setStatusMessage("Text successfully extracted via " + extractionResult.getExtractionMethod());
            document = documentRepository.save(document);

            log.info("Document {} successfully processed in {} ms. Extracted {} words.",
                    document.getId(), duration, metadata.getWordCount());

        } catch (Exception ex) {
            log.error("Failed to extract text from document {}: {}", originalFilename, ex.getMessage(), ex);
            document.setStatus(DocumentStatus.FAILED);
            document.setStatusMessage("Extraction failed: " + ex.getMessage());
            document = documentRepository.save(document);
        }

        return mapToUploadResponse(document);
    }

    @Override
    public LegalDocument getDocument(String id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with ID: " + id));
    }

    @Override
    public DocumentMetadataResponse getDocumentById(String id, String userId) {
        LegalDocument doc = findDocumentWithOwnership(id, userId);
        return mapToMetadataResponse(doc);
    }

    @Override
    public DocumentContentResponse getDocumentContent(String id, String userId) {
        LegalDocument doc = findDocumentWithOwnership(id, userId);
        int wordCount = doc.getMetadata() != null ? doc.getMetadata().getWordCount() : 0;
        int charCount = doc.getMetadata() != null ? doc.getMetadata().getCharacterCount() : 0;
        return new DocumentContentResponse(
                doc.getId(),
                doc.getTitle(),
                doc.getOriginalFileName(),
                doc.getStatus(),
                wordCount,
                charCount,
                doc.getExtractedText() != null ? doc.getExtractedText() : ""
        );
    }

    @Override
    public List<DocumentMetadataResponse> getUserDocuments(String userId) {
        List<LegalDocument> docs;
        if (userId != null && !userId.isBlank()) {
            docs = documentRepository.findByUserIdOrderByUploadedAtDesc(userId);
        } else {
            docs = documentRepository.findAll(Sort.by(Sort.Direction.DESC, "uploadedAt"));
        }
        return docs.stream().map(this::mapToMetadataResponse).toList();
    }

    @Override
    public void deleteDocument(String id, String userId) {
        LegalDocument doc = findDocumentWithOwnership(id, userId);

        // Delete physical file
        if (doc.getStoragePath() != null) {
            try {
                Path filePath = Paths.get(doc.getStoragePath());
                Files.deleteIfExists(filePath);
                log.info("Deleted physical file: {}", filePath);
            } catch (IOException e) {
                log.warn("Could not delete file from disk: {}", e.getMessage());
            }
        }

        documentRepository.delete(doc);
        log.info("Deleted document {} from repository.", id);
    }

    @Override
    public OcrExtractionResponse performOcr(MultipartFile file) {
        validateFile(file);
        try {
            return groqVisionOcrService.extractTextFromImage(file);
        } catch (IOException e) {
            return OcrExtractionResponse.failure("Failed to read image bytes: " + e.getMessage());
        }
    }

    // --- Private Helper Methods ---

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Uploaded file cannot be empty.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("File size exceeds 15MB maximum limit.");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new InvalidFileException("File must have a valid name.");
        }
        if (filename.contains("..")) {
            throw new InvalidFileException("Filename contains invalid path sequence: " + filename);
        }
    }

    private DocumentExtractor findExtractor(String contentType, String filename) {
        return extractors.stream()
                .filter(e -> e.supports(contentType, filename))
                .findFirst()
                .orElseThrow(() -> new InvalidFileException(
                        "Unsupported file format. Allowed formats: PDF, DOCX, TXT, and scanned image formats."));
    }

    private boolean isImageFile(String contentType, String filename) {
        if (contentType != null && contentType.startsWith("image/")) {
            return true;
        }
        if (filename != null) {
            String lower = filename.toLowerCase();
            return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".webp");
        }
        return false;
    }

    private String computeSha256(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.warn("Could not compute SHA-256 hash: {}", e.getMessage());
            return "UNKNOWN";
        }
    }

    private LegalDocument findDocumentWithOwnership(String id, String userId) {
        Optional<LegalDocument> docOpt;
        if (userId != null && !userId.isBlank()) {
            docOpt = documentRepository.findByIdAndUserId(id, userId);
        } else {
            docOpt = documentRepository.findById(id);
        }
        return docOpt.orElseThrow(() -> new DocumentNotFoundException("Document not found with ID: " + id));
    }

    private DocumentUploadResponse mapToUploadResponse(LegalDocument doc) {
        DocumentUploadResponse resp = new DocumentUploadResponse();
        resp.setDocumentId(doc.getId());
        resp.setTitle(doc.getTitle());
        resp.setOriginalFileName(doc.getOriginalFileName());
        resp.setStatus(doc.getStatus());
        resp.setStatusMessage(doc.getStatusMessage());
        resp.setMetadata(doc.getMetadata());
        resp.setUploadedAt(doc.getUploadedAt());

        if (doc.getExtractedText() != null) {
            String text = doc.getExtractedText().trim();
            resp.setTextPreview(text.length() > 300 ? text.substring(0, 300) + "..." : text);
        }
        return resp;
    }

    private DocumentMetadataResponse mapToMetadataResponse(LegalDocument doc) {
        DocumentMetadataResponse resp = new DocumentMetadataResponse();
        resp.setId(doc.getId());
        resp.setUserId(doc.getUserId());
        resp.setTitle(doc.getTitle());
        resp.setOriginalFileName(doc.getOriginalFileName());
        resp.setStatus(doc.getStatus());
        resp.setStatusMessage(doc.getStatusMessage());
        resp.setMetadata(doc.getMetadata());
        resp.setUploadedAt(doc.getUploadedAt());
        resp.setUpdatedAt(doc.getUpdatedAt());
        return resp;
    }
}
