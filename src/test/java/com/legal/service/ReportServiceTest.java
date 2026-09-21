package com.legal.service;

import com.legal.model.LegalDocument;
import com.legal.model.LegalReport;
import com.legal.repository.AnalysisRepository;
import com.legal.repository.DocumentRepository;
import com.legal.repository.ReportRepository;
import com.legal.service.impl.ReportServiceImpl;
import com.legal.service.report.PdfReportExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test suite for ReportService compiling audit reports.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module).
 */
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private AnalysisRepository analysisRepository;

    @Mock
    private PdfReportExporter pdfReportExporter;

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reportService = new ReportServiceImpl(reportRepository, documentRepository, analysisRepository, pdfReportExporter);
    }

    @Test
    void testGenerateReportForDocument_Success() {
        LegalDocument doc = new LegalDocument();
        doc.setId("doc-100");
        doc.setFilename("Employment_Contract.pdf");
        doc.setUserId("user-1");

        when(documentRepository.findById("doc-100")).thenReturn(Optional.of(doc));
        when(analysisRepository.findByDocumentId("doc-100")).thenReturn(Optional.empty());
        when(reportRepository.findByDocumentId("doc-100")).thenReturn(Optional.empty());
        when(reportRepository.save(any(LegalReport.class))).thenAnswer(i -> i.getArgument(0));

        LegalReport report = reportService.generateReportForDocument("doc-100", "user-1");

        assertNotNull(report);
        assertEquals("doc-100", report.getDocumentId());
        assertEquals("Employment_Contract.pdf", report.getDocumentTitle());
        verify(reportRepository, times(1)).save(any(LegalReport.class));
    }
}
