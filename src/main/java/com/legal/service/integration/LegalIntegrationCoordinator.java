package com.legal.service.integration;

import com.legal.model.LegalDocument;
import com.legal.model.LegalReport;
import com.legal.service.AnalyticsService;
import com.legal.service.DocumentService;
import com.legal.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Full-stack integration coordinator orchestrating document processing pipelines,
 * risk analysis triggers, AI contextual Q&A grounding, and audit report generation.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module Lead).
 */
@Service
public class LegalIntegrationCoordinator {

    private final DocumentService documentService;
    private final ReportService reportService;
    private final AnalyticsService analyticsService;

    @Autowired
    public LegalIntegrationCoordinator(DocumentService documentService,
                                       ReportService reportService,
                                       AnalyticsService analyticsService) {
        this.documentService = documentService;
        this.reportService = reportService;
        this.analyticsService = analyticsService;
    }

    /**
     * Executes end-to-end integration workflow for an ingested document.
     */
    public LegalReport processAndCompileReport(String documentId, String userId) {
        // 1. Verify document state from Gourav's module
        LegalDocument doc = documentService.getDocument(documentId);

        // 2. Trigger/compile audit report from Harshvardhan's module
        LegalReport report = reportService.generateReportForDocument(documentId, userId != null ? userId : doc.getUserId());

        // 3. Refresh global analytics cache
        analyticsService.getGlobalDashboardMetrics();

        return report;
    }
}
