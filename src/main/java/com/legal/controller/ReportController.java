package com.legal.controller;

import com.legal.dto.ApiResponse;
import com.legal.dto.AuditReportResponse;
import com.legal.dto.ReportSummaryResponse;
import com.legal.model.LegalReport;
import com.legal.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing endpoints for audit reports, summaries, and PDF downloads.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module).
 */
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/{documentId}/generate")
    public ResponseEntity<ApiResponse<LegalReport>> generateReport(
            @PathVariable String documentId,
            @RequestParam(required = false) String userId) {
        LegalReport report = reportService.generateReportForDocument(documentId, userId);
        return ResponseEntity.ok(ApiResponse.success("Legal audit report compiled successfully", report));
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<ApiResponse<AuditReportResponse>> getAuditReport(@PathVariable String documentId) {
        AuditReportResponse reportDto = reportService.getAuditReportDto(documentId);
        return ResponseEntity.ok(ApiResponse.success("Audit report details fetched successfully", reportDto));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ReportSummaryResponse>>> getUserReports(@PathVariable String userId) {
        List<ReportSummaryResponse> summaries = reportService.getUserReportSummaries(userId);
        return ResponseEntity.ok(ApiResponse.success("User report summaries retrieved successfully", summaries));
    }

    @GetMapping("/{documentId}/pdf")
    public ResponseEntity<byte[]> downloadPdfReport(@PathVariable String documentId) {
        byte[] pdfBytes = reportService.exportReportPdf(documentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Legal_Audit_Report_" + documentId + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdfBytes.length)
                .body(pdfBytes);
    }
}
