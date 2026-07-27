package com.legal.service.impl;

import com.legal.dto.DashboardMetricsResponse;
import com.legal.dto.RiskDistributionResponse;
import com.legal.model.AnalysisRecord;
import com.legal.model.LegalDocument;
import com.legal.repository.AnalysisRepository;
import com.legal.repository.DocumentRepository;
import com.legal.repository.ReportRepository;
import com.legal.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation aggregating analytics metrics across documents, risk analyses, and reports.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module).
 */
@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final DocumentRepository documentRepository;
    private final AnalysisRepository analysisRepository;
    private final ReportRepository reportRepository;

    @Autowired
    public AnalyticsServiceImpl(DocumentRepository documentRepository,
                                AnalysisRepository analysisRepository,
                                ReportRepository reportRepository) {
        this.documentRepository = documentRepository;
        this.analysisRepository = analysisRepository;
        this.reportRepository = reportRepository;
    }

    @Override
    public DashboardMetricsResponse getGlobalDashboardMetrics() {
        List<LegalDocument> documents = documentRepository.findAll();
        List<AnalysisRecord> analyses = analysisRepository.findAll();

        return buildMetrics(documents, analyses);
    }

    @Override
    public DashboardMetricsResponse getUserDashboardMetrics(String userId) {
        List<LegalDocument> documents = documentRepository.findByUserId(userId);
        // Find analyses for documents owned by user
        List<String> userDocIds = documents.stream().map(LegalDocument::getId).toList();
        List<AnalysisRecord> analyses = analysisRepository.findAll().stream()
                .filter(a -> userDocIds.contains(a.getDocumentId()))
                .toList();

        return buildMetrics(documents, analyses);
    }

    @Override
    public RiskDistributionResponse getRiskDistribution(String userId) {
        DashboardMetricsResponse metrics = (userId != null && !userId.isBlank())
                ? getUserDashboardMetrics(userId)
                : getGlobalDashboardMetrics();

        Map<String, Long> dist = metrics.getRiskDistribution();
        long low = dist.getOrDefault("LOW", 0L);
        long medium = dist.getOrDefault("MEDIUM", 0L);
        long high = dist.getOrDefault("HIGH", 0L);
        long critical = dist.getOrDefault("CRITICAL", 0L);

        return new RiskDistributionResponse(low, medium, high, critical, metrics.getAverageRiskScore());
    }

    private DashboardMetricsResponse buildMetrics(List<LegalDocument> documents, List<AnalysisRecord> analyses) {
        long totalDocs = documents.size();
        long totalClauses = 0;
        long highRiskCount = 0;
        double sumRiskScore = 0;
        int scoreCount = 0;

        Map<String, Long> riskDist = new HashMap<>();
        riskDist.put("LOW", 0L);
        riskDist.put("MEDIUM", 0L);
        riskDist.put("HIGH", 0L);
        riskDist.put("CRITICAL", 0L);

        Map<String, Long> docTypes = new HashMap<>();

        for (LegalDocument doc : documents) {
            String type = doc.getMetadata() != null && doc.getMetadata().getFileType() != null
                    ? doc.getMetadata().getFileType().toUpperCase()
                    : "UNKNOWN";
            docTypes.put(type, docTypes.getOrDefault(type, 0L) + 1);
        }

        for (AnalysisRecord analysis : analyses) {
            if (analysis.getClauses() != null) {
                totalClauses += analysis.getClauses().size();
            }

            if (analysis.getRiskFlags() != null) {
                long highInAnalysis = analysis.getRiskFlags().stream()
                        .filter(flag -> "HIGH".equalsIgnoreCase(flag.getSeverity()) || "CRITICAL".equalsIgnoreCase(flag.getSeverity()))
                        .count();
                highRiskCount += highInAnalysis;
            }

            if (analysis.getOverallRiskScore() > 0) {
                sumRiskScore += analysis.getOverallRiskScore();
                scoreCount++;

                String category = categorizeScore(analysis.getOverallRiskScore());
                riskDist.put(category, riskDist.getOrDefault(category, 0L) + 1);
            }
        }

        double avgScore = scoreCount > 0 ? (sumRiskScore / scoreCount) : 0.0;

        return new DashboardMetricsResponse(
                totalDocs,
                totalClauses,
                highRiskCount,
                Math.round(avgScore * 10.0) / 10.0,
                riskDist,
                docTypes
        );
    }

    private String categorizeScore(double score) {
        if (score >= 80) return "CRITICAL";
        if (score >= 50) return "HIGH";
        if (score >= 25) return "MEDIUM";
        return "LOW";
    }
}
