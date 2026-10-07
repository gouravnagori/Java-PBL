package com.legal.service;

import com.legal.dto.DocumentContentResponse;
import com.legal.dto.DocumentMetadataResponse;
import com.legal.dto.DocumentUploadResponse;
import com.legal.exception.DocumentNotFoundException;
import com.legal.exception.InvalidFileException;
import com.legal.model.DocumentStatus;
import com.legal.model.LegalDocument;
import com.legal.repository.DocumentRepository;
import com.legal.service.extractor.DocumentExtractor;
import com.legal.service.extractor.ExtractionResult;
import com.legal.service.impl.DocumentServiceImpl;
import com.legal.service.ocr.GroqVisionOcrService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentExtractor mockExtractor;

    @TempDir
    Path tempUploadDir;

    private DocumentServiceImpl documentService;

    @BeforeEach
    void setUp() {
        GroqVisionOcrService groqVisionOcrService = new GroqVisionOcrService(new org.springframework.web.client.RestTemplate(), new com.fasterxml.jackson.databind.ObjectMapper());
        documentService = new DocumentServiceImpl(
                documentRepository,
                List.of(mockExtractor),
                groqVisionOcrService,
                tempUploadDir.toString()
        );
    }

    @Test
    void testUploadEmptyFileThrowsInvalidFileException() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.pdf", "application/pdf", new byte[0]);

        assertThrows(InvalidFileException.class, () ->
                documentService.uploadAndProcessDocument(emptyFile, "Test Title", "user123"));
    }

    @Test
    void testUploadOversizedFileThrowsInvalidFileException() {
        byte[] largeBytes = new byte[16 * 1024 * 1024]; // 16MB > 15MB limit
        MockMultipartFile largeFile = new MockMultipartFile(
                "file", "large.pdf", "application/pdf", largeBytes);

        assertThrows(InvalidFileException.class, () ->
                documentService.uploadAndProcessDocument(largeFile, "Large File", "user123"));
    }

    @Test
    void testSuccessfulUploadAndProcessing() throws IOException {
        byte[] content = "This is a legal rental agreement between landlord and tenant.".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "lease.txt", "text/plain", content);

        when(mockExtractor.supports("text/plain", "lease.txt")).thenReturn(true);
        when(mockExtractor.extract(any(InputStream.class), eq("lease.txt")))
                .thenReturn(new ExtractionResult("Extracted text from lease", 1, "MOCK_EXTRACTOR"));

        when(documentRepository.save(any(LegalDocument.class))).thenAnswer(invocation -> {
            LegalDocument doc = invocation.getArgument(0);
            if (doc.getId() == null) {
                doc.setId("doc-uuid-123");
            }
            return doc;
        });

        DocumentUploadResponse response = documentService.uploadAndProcessDocument(mockFile, "Lease Agreement", "user123");

        assertNotNull(response);
        assertEquals("doc-uuid-123", response.getDocumentId());
        assertEquals("Lease Agreement", response.getTitle());
        assertEquals(DocumentStatus.PROCESSED, response.getStatus());
        assertNotNull(response.getMetadata());
        assertEquals("lease.txt", response.getMetadata().getFileName());
        assertEquals(1, response.getMetadata().getPageCount());
        verify(documentRepository, atLeast(2)).save(any(LegalDocument.class));
    }

    @Test
    void testGetDocumentByIdNotFoundThrowsException() {
        when(documentRepository.findByIdAndUserId("nonexistent", "user123")).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () ->
                documentService.getDocumentById("nonexistent", "user123"));
    }

    @Test
    void testGetDocumentContent() {
        LegalDocument doc = new LegalDocument();
        doc.setId("doc-1");
        doc.setTitle("NDA");
        doc.setOriginalFileName("nda.pdf");
        doc.setStatus(DocumentStatus.PROCESSED);
        doc.setExtractedText("Confidentiality agreement clause 1...");

        when(documentRepository.findByIdAndUserId("doc-1", "user123")).thenReturn(Optional.of(doc));

        DocumentContentResponse content = documentService.getDocumentContent("doc-1", "user123");
        assertNotNull(content);
        assertEquals("doc-1", content.getDocumentId());
        assertEquals("Confidentiality agreement clause 1...", content.getExtractedText());
    }
}
