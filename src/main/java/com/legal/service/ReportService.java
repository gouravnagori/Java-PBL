package com.legal.service;

import com.legal.dto.AuditReportResponse;
import com.legal.dto.ReportSummaryResponse;
import com.legal.model.LegalReport;

import java.util.List;

/**
 * Service orchestrator interface for building, compiling, and exporting legal audit reports.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module).
 */
public interface ReportService {

    /**
     * Generate or compile a structured LegalReport for a given document.
     */
    LegalReport generateReportForDocument(String documentId, String userId);

    /**
     * Fetch report by document ID.
     */
    LegalReport getReportByDocumentId(String documentId);

    /**
     * Retrieve AuditReportResponse DTO payload for API response.
     */
    AuditReportResponse getAuditReportDto(String documentId);

    /**
     * List report summaries for a user.
     */
    List<ReportSummaryResponse> getUserReportSummaries(String userId);

    /**
     * List all report summaries across the platform (Admin Control Panel).
     */
    List<ReportSummaryResponse> getAllReportSummaries();

    /**
     * Export PDF report binary data for document.
     */
    byte[] exportReportPdf(String documentId);
}
