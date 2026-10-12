package com.legal.service.impl;

import com.legal.dto.AuditReportResponse;
import com.legal.dto.ReportSummaryResponse;
import com.legal.exception.DocumentNotFoundException;
import com.legal.model.AnalysisRecord;
import com.legal.model.LegalDocument;
import com.legal.model.LegalReport;
import com.legal.repository.AnalysisRepository;
import com.legal.repository.DocumentRepository;
import com.legal.repository.ReportRepository;
import com.legal.service.ReportService;
import com.legal.service.report.PdfReportExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Service implementation orchestrating report generation, persistence, and PDF exports.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module).
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final DocumentRepository documentRepository;
    private final AnalysisRepository analysisRepository;
    private final PdfReportExporter pdfReportExporter;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository,
                             DocumentRepository documentRepository,
                             AnalysisRepository analysisRepository,
                             PdfReportExporter pdfReportExporter) {
        this.reportRepository = reportRepository;
        this.documentRepository = documentRepository;
        this.analysisRepository = analysisRepository;
        this.pdfReportExporter = pdfReportExporter;
    }

    @Override
    public LegalReport generateReportForDocument(String documentId, String userId) {
        LegalDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with ID: " + documentId));

        Optional<AnalysisRecord> analysisOpt = analysisRepository.findByDocumentId(documentId);

        double riskScore = 0.0;
        String riskCategory = "LOW";
        List<String> findings = new ArrayList<>();
        Map<String, Integer> severityBreakdown = new HashMap<>();
        severityBreakdown.put("LOW", 0);
        severityBreakdown.put("MEDIUM", 0);
        severityBreakdown.put("HIGH", 0);
        severityBreakdown.put("CRITICAL", 0);

        String execSummary = "Preliminary analysis completed for contract: " + doc.getFilename();

        if (analysisOpt.isPresent()) {
            AnalysisRecord record = analysisOpt.get();
            riskScore = record.getOverallRiskScore();
            riskCategory = categorizeScore(riskScore);
            if (record.getExecutiveSummary() != null && !record.getExecutiveSummary().isBlank()) {
                execSummary = record.getExecutiveSummary();
            }

            if (record.getRiskFlags() != null) {
                for (AnalysisRecord.RiskFlag flag : record.getRiskFlags()) {
                    String sev = flag.getSeverity() != null ? flag.getSeverity().toUpperCase() : "MEDIUM";
                    severityBreakdown.put(sev, severityBreakdown.getOrDefault(sev, 0) + 1);
                    findings.add("[" + sev + "] " + flag.getClauseType() + ": " + flag.getDescription());
                }
            }
        }

        List<String> recommendations = List.of(
                "Review all flagged high-severity risk clauses with a licensed legal counsel.",
                "Negotiate liability cap terms to prevent unlimited financial exposure.",
                "Ensure governing jurisdiction matches local legal compliance standards."
        );

        String disclaimer = "INFORMATIONAL NOTICE: This legal audit report is produced by automated NLP analysis and does not constitute formal legal representation.";

        LegalReport report = reportRepository.findByDocumentId(documentId)
                .orElse(new LegalReport());

        report.setDocumentId(documentId);
        report.setUserId(userId != null ? userId : doc.getUserId());
        report.setDocumentTitle(doc.getFilename());
        report.setDocumentType(doc.getMetadata() != null ? doc.getMetadata().getFileType() : "LEGAL_CONTRACT");
        report.setOverallRiskScore(riskScore);
        report.setRiskCategory(riskCategory);
        report.setExecutiveSummary(execSummary);
        report.setKeyRiskFindings(findings);
        report.setSeverityBreakdown(severityBreakdown);
        report.setRecommendedNextSteps(recommendations);
        report.setDisclaimerNotice(disclaimer);
        report.setPdfStoragePath("/api/reports/" + documentId + "/pdf");

        return reportRepository.save(report);
    }

    @Override
    public LegalReport getReportByDocumentId(String documentId) {
        return reportRepository.findByDocumentId(documentId)
                .orElseGet(() -> generateReportForDocument(documentId, null));
    }

    @Override
    public AuditReportResponse getAuditReportDto(String documentId) {
        LegalReport report = getReportByDocumentId(documentId);
        return new AuditReportResponse(
                report.getId(),
                report.getDocumentId(),
                report.getDocumentTitle(),
                report.getDocumentType(),
                report.getOverallRiskScore(),
                report.getRiskCategory(),
                report.getExecutiveSummary(),
                report.getKeyRiskFindings(),
                report.getSeverityBreakdown(),
                report.getRecommendedNextSteps(),
                report.getDisclaimerNotice(),
                report.getCreatedAt()
        );
    }

    @Override
    public List<ReportSummaryResponse> getUserReportSummaries(String userId) {
        List<LegalReport> reports = (userId != null && !userId.isBlank())
                ? reportRepository.findByUserId(userId)
                : reportRepository.findAll();

        return reports.stream().map(r -> new ReportSummaryResponse(
                r.getId(),
                r.getDocumentId(),
                r.getDocumentTitle(),
                r.getDocumentType(),
                r.getOverallRiskScore(),
                r.getRiskCategory(),
                r.getKeyRiskFindings() != null ? r.getKeyRiskFindings().size() : 0,
                r.getSeverityBreakdown() != null ? r.getSeverityBreakdown().getOrDefault("HIGH", 0) : 0,
                "/api/reports/" + r.getDocumentId() + "/pdf",
                r.getCreatedAt(),
                r.getUserId()
        )).toList();
    }

    @Override
    public List<ReportSummaryResponse> getAllReportSummaries() {
        // Automatically ensure any documents in the system have compiled reports
        try {
            List<LegalDocument> allDocs = documentRepository.findAll();
            for (LegalDocument doc : allDocs) {
                if (reportRepository.findByDocumentId(doc.getId()).isEmpty()) {
                    try {
                        generateReportForDocument(doc.getId(), doc.getUserId());
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {}
        return getUserReportSummaries(null);
    }

    @Override
    public byte[] exportReportPdf(String documentId) {
        LegalReport report = getReportByDocumentId(documentId);
        LegalDocument document = documentRepository.findById(documentId).orElse(null);
        AnalysisRecord analysis = analysisRepository.findByDocumentId(documentId).orElse(null);

        try {
            return pdfReportExporter.generatePdfReport(report, document, analysis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to render PDF report for document: " + documentId, e);
        }
    }

    private String categorizeScore(double score) {
        if (score >= 80) return "CRITICAL";
        if (score >= 50) return "HIGH";
        if (score >= 25) return "MEDIUM";
        return "LOW";
    }
}
