package com.legal.service;

import com.legal.dto.DashboardMetricsResponse;
import com.legal.model.AnalysisRecord;
import com.legal.model.LegalDocument;
import com.legal.repository.AnalysisRepository;
import com.legal.repository.DocumentRepository;
import com.legal.repository.ReportRepository;
import com.legal.service.impl.AnalyticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test suite for AnalyticsService metrics aggregation.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module).
 */
class AnalyticsServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private AnalysisRepository analysisRepository;

    @Mock
    private ReportRepository reportRepository;

    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        analyticsService = new AnalyticsServiceImpl(documentRepository, analysisRepository, reportRepository);
    }

    @Test
    void testGetGlobalDashboardMetrics_Success() {
        LegalDocument doc1 = new LegalDocument();
        doc1.setId("doc-1");
        doc1.setFilename("NDA_Agreement.pdf");

        AnalysisRecord analysis1 = new AnalysisRecord();
        analysis1.setDocumentId("doc-1");
        analysis1.setOverallRiskScore(45.0);

        when(documentRepository.findAll()).thenReturn(List.of(doc1));
        when(analysisRepository.findAll()).thenReturn(List.of(analysis1));

        DashboardMetricsResponse response = analyticsService.getGlobalDashboardMetrics();

        assertNotNull(response);
        assertEquals(1, response.getTotalDocumentsProcessed());
        assertEquals(45.0, response.getAverageRiskScore());
    }
}
