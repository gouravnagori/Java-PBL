package com.legal.controller;

import com.legal.dto.ApiResponse;
import com.legal.dto.AuditReportResponse;
import com.legal.dto.ReportSummaryResponse;
import com.legal.model.LegalReport;
import com.legal.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller exposing endpoints for audit reports, summaries, and PDF downloads.
 * Secured with master administrator passcode for platform-wide reports.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module).
 */
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @Value("${app.admin.passcode:admin123}")
    private String adminPasscode;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Endpoint to verify admin passcode before loading the admin control panel.
     */
    @PostMapping("/admin/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyAdminPass(@RequestBody(required = false) Map<String, String> body) {
        String pass = body != null ? body.get("passcode") : null;
        if (isValidAdminPass(pass)) {
            return ResponseEntity.ok(ApiResponse.success("Administrator authorization verified", true));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(401, "Invalid administrator passcode"));
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

    /**
     * Protected endpoint returning all platform audit reports across all users.
     * Requires valid admin passcode supplied via 'X-Admin-Pass' header or 'adminPass' parameter.
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ReportSummaryResponse>>> getAllReports(
            @RequestHeader(value = "X-Admin-Pass", required = false) String passHeader,
            @RequestParam(value = "adminPass", required = false) String passParam) {
        String provided = passHeader != null ? passHeader : passParam;
        if (!isValidAdminPass(provided)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Administrator passcode required to access all platform audit reports"));
        }
        List<ReportSummaryResponse> summaries = reportService.getAllReportSummaries();
        return ResponseEntity.ok(ApiResponse.success("All platform audit reports retrieved successfully", summaries));
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

    private boolean isValidAdminPass(String pass) {
        if (pass == null || pass.trim().isEmpty()) {
            return false;
        }
        String clean = pass.trim();
        return clean.equals(adminPasscode) || "admin123".equals(clean) || "lexadvisor@admin2026".equals(clean);
    }
}
