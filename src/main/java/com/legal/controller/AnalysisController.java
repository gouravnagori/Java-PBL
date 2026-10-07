package com.legal.controller;

import com.legal.dto.AnalysisReportResponse;
import com.legal.dto.ApiResponse;
import com.legal.dto.RiskDto;
import com.legal.model.Analysis;
import com.legal.service.AnalysisService;
import com.legal.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for the Legal Analysis & Risk Detection Engine.
 *
 * Exposes the three core API contracts:
 * <ul>
 *   <li>POST /api/analysis/{documentId}           — Execute legal clause parsing and risk scoring.</li>
 *   <li>GET  /api/analysis/{documentId}            — Retrieve complete analysis report.</li>
 *   <li>GET  /api/analysis/{documentId}/risks      — Retrieve HIGH & CRITICAL risk highlights.</li>
 *   <li>GET  /api/analysis                         — List all analyses for the current user.</li>
 *   <li>DELETE /api/analysis/{documentId}          — Delete analysis and all associated data.</li>
 *   <li>GET  /api/analysis/health                  — Health check endpoint.</li>
 * </ul>
 *
 * Owned by Dilip Kumawat — Legal Analysis & Risk Detection Module.
 */
@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class AnalysisController {

    private static final Logger log = LoggerFactory.getLogger(AnalysisController.class);

    private final AnalysisService analysisService;
    private final DocumentService documentService;

    public AnalysisController(AnalysisService analysisService, DocumentService documentService) {
        this.analysisService = analysisService;
        this.documentService = documentService;
    }

    // -------------------------------------------------------------------------
    // Helper: resolve authenticated user ID
    // -------------------------------------------------------------------------

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return auth.getName();
    }

    // =========================================================================
    // Core Endpoints
    // =========================================================================

    /**
     * POST /api/analysis/{documentId}
     *
     * Triggers the full legal analysis pipeline for a previously uploaded document.
     * The service fetches the extracted text from Gourav's DocumentService,
     * runs clause segmentation, risk scoring, and persists the result.
     *
     * @param documentId the ID of the uploaded LegalDocument.
     * @return HTTP 201 with the complete {@link AnalysisReportResponse}.
     */
    @PostMapping("/{documentId}")
    public ResponseEntity<ApiResponse<AnalysisReportResponse>> runAnalysis(
            @PathVariable String documentId) {

        String userId = getCurrentUserId();
        log.info("Analysis requested for documentId={}, userId={}", documentId, userId);

        // Fetch extracted text from Gourav's Document module
        String extractedText = documentService.getDocumentContent(documentId, userId).getExtractedText();

        if (extractedText == null || extractedText.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error("Document has no extractable text content."));
        }

        AnalysisReportResponse report = analysisService.runAnalysis(documentId, extractedText, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Legal analysis completed successfully.", report));
    }

    /**
     * GET /api/analysis/{documentId}
     *
     * Retrieves the complete legal analysis report for a document, including all
     * detected clauses and their associated risk flags.
     *
     * @param documentId the LegalDocument ID.
     * @return HTTP 200 with the full {@link AnalysisReportResponse}.
     */
    @GetMapping("/{documentId}")
    public ResponseEntity<ApiResponse<AnalysisReportResponse>> getAnalysisReport(
            @PathVariable String documentId) {

        String userId = getCurrentUserId();
        log.info("Fetching analysis report for documentId={}, userId={}", documentId, userId);

        AnalysisReportResponse report = analysisService.getReport(documentId, userId);
        return ResponseEntity.ok(ApiResponse.success("Analysis report retrieved.", report));
    }

    /**
     * GET /api/analysis/{documentId}/risks
     *
     * Retrieves only the HIGH and CRITICAL risk highlights for a document.
     * Designed for dashboard summary cards and the AI Advisor module's risk context.
     *
     * @param documentId the LegalDocument ID.
     * @return HTTP 200 with a list of high-priority {@link RiskDto} objects.
     */
    @GetMapping("/{documentId}/risks")
    public ResponseEntity<ApiResponse<List<RiskDto>>> getHighPriorityRisks(
            @PathVariable String documentId) {

        String userId = getCurrentUserId();
        log.info("Fetching high-priority risks for documentId={}, userId={}", documentId, userId);

        List<RiskDto> risks = analysisService.getHighPriorityRisks(documentId, userId);
        return ResponseEntity.ok(ApiResponse.success(
            "High and critical risk highlights retrieved. Total: " + risks.size(), risks));
    }

    /**
     * GET /api/analysis
     *
     * Lists all analysis records created by the authenticated user, ordered most recent first.
     *
     * @return HTTP 200 with a list of {@link Analysis} metadata records.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Analysis>>> listUserAnalyses() {
        String userId = getCurrentUserId();
        List<Analysis> analyses = analysisService.getUserAnalyses(userId);
        return ResponseEntity.ok(ApiResponse.success(
            "Analysis history retrieved. Total: " + analyses.size(), analyses));
    }

    /**
     * DELETE /api/analysis/{documentId}
     *
     * Deletes the analysis record and all associated clauses and risk flags for the document.
     *
     * @param documentId the LegalDocument ID.
     * @return HTTP 200 on success.
     */
    @DeleteMapping("/{documentId}")
    public ResponseEntity<ApiResponse<Void>> deleteAnalysis(@PathVariable String documentId) {
        String userId = getCurrentUserId();
        log.info("Delete analysis requested for documentId={}, userId={}", documentId, userId);
        analysisService.deleteAnalysis(documentId, userId);
        return ResponseEntity.ok(ApiResponse.success("Analysis deleted successfully.", null));
    }

    /**
     * GET /api/analysis/health
     *
     * Module health-check endpoint for monitoring and integration verification.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "module", "Legal Analysis & Risk Detection Engine",
            "lead", "Dilip Kumawat",
            "status", "UP",
            "features", List.of(
                "Document Classification (DocType)",
                "Regex-Based Clause Segmentation",
                "13-Rule Risk Detection Engine",
                "Severity Matrix: LOW / MEDIUM / HIGH / CRITICAL",
                "Composite Risk Score (0-100)",
                "Plain-Language Explanations & Suggestions",
                "MongoDB Persistence"
            )
        ));
    }
}
