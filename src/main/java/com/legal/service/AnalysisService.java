package com.legal.service;

import com.legal.dto.*;
import com.legal.model.Analysis;
import com.legal.model.Clause;
import com.legal.model.Risk;
import com.legal.model.enums.AnalysisStatus;
import com.legal.model.enums.DocType;
import com.legal.model.enums.Severity;
import com.legal.repository.AnalysisResultRepository;
import com.legal.repository.ClauseRepository;
import com.legal.repository.RiskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Orchestration Service for the Legal Analysis & Risk Detection pipeline.
 *
 * Pipeline flow:
 * <pre>
 *   Extracted Text (from Gourav's DocumentService)
 *       → ClassificationService      (DocType detection)
 *       → Clause Segmenter           (regex boundary splitting)
 *       → RiskService.evaluateClause (per-clause risk rules)
 *       → Composite score & summary  (RiskService)
 *       → MongoDB persistence        (Analysis + Clause + Risk entities)
 * </pre>
 *
 * Key API contracts:
 * - POST /api/analysis/{documentId}  → runAnalysis()
 * - GET  /api/analysis/{documentId}  → getReport()
 * - GET  /api/analysis/{documentId}/risks → getHighPriorityRisks()
 *
 * Owned by Dilip Kumawat — Legal Analysis & Risk Detection Module.
 */
@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    // -----------------------------------------------------------------------
    // Clause Segmentation: paragraph boundary patterns
    // -----------------------------------------------------------------------

    /**
     * Primary segmentation: splits on two or more newlines (blank-line separated paragraphs).
     */
    private static final Pattern PARA_SPLIT = Pattern.compile("\\n{2,}");

    /**
     * Secondary segmentation: splits numbered / lettered clause headings.
     * Matches patterns like "1.", "1.2", "Section 3", "CLAUSE IV", etc.
     */
    private static final Pattern CLAUSE_HEADING = Pattern.compile(
        "(?m)^(?:(?:section|clause|article|schedule)\\s+\\S+|\\d+(?:\\.\\d+)*\\.?)\\s+",
        Pattern.CASE_INSENSITIVE);

    /** Minimum number of characters for a text segment to be treated as a meaningful clause. */
    private static final int MIN_CLAUSE_LENGTH = 40;

    /** Maximum clause length stored in the DB (truncated for storage efficiency). */
    private static final int MAX_CLAUSE_LENGTH = 2000;

    // -----------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------

    private final AnalysisResultRepository analysisRepo;
    private final ClauseRepository clauseRepo;
    private final RiskRepository riskRepo;
    private final ClassificationService classificationService;
    private final RiskService riskService;

    public AnalysisService(AnalysisResultRepository analysisRepo,
                           ClauseRepository clauseRepo,
                           RiskRepository riskRepo,
                           ClassificationService classificationService,
                           RiskService riskService) {
        this.analysisRepo         = analysisRepo;
        this.clauseRepo           = clauseRepo;
        this.riskRepo             = riskRepo;
        this.classificationService = classificationService;
        this.riskService           = riskService;
    }

    // =========================================================================
    // Public API
    // =========================================================================

    /**
     * Executes the full legal analysis pipeline against the extracted text of a document.
     *
     * Steps:
     * 1. Create an Analysis record in PENDING state.
     * 2. Classify the document type.
     * 3. Segment the text into clauses.
     * 4. For each clause, run risk evaluation rules.
     * 5. Persist all Clause and Risk entities.
     * 6. Compute composite risk score and summary.
     * 7. Update Analysis record to COMPLETED.
     *
     * @param documentId    the ID of the LegalDocument (from Gourav's module).
     * @param extractedText the raw text extracted from the document.
     * @param userId        the authenticated owner's user ID.
     * @return the completed {@link AnalysisReportResponse}.
     */
    public AnalysisReportResponse runAnalysis(String documentId, String extractedText, String userId) {
        log.info("Starting legal analysis pipeline for documentId={}, userId={}", documentId, userId);

        // ---- 1. Create Analysis record in PENDING state ----
        Analysis analysis = createPendingAnalysis(documentId, userId);

        try {
            // ---- 2. Transition to ANALYSING ----
            analysis.setStatus(AnalysisStatus.ANALYSING);
            analysisRepo.save(analysis);

            // ---- 3. Classify document type ----
            DocType docType = classificationService.classify(extractedText);
            analysis.setDocType(docType);
            log.info("Document {} classified as {}", documentId, docType);

            // ---- 4. Segment into clauses ----
            List<String> segments = segmentText(extractedText);
            log.info("Segmented {} clauses from documentId={}", segments.size(), documentId);

            // ---- 5. Evaluate risks per clause ----
            List<Clause> savedClauses = new ArrayList<>();
            List<Risk> allRisks       = new ArrayList<>();

            for (int i = 0; i < segments.size(); i++) {
                String segment = segments.get(i);
                Clause clause = new Clause(analysis.getId(), userId,
                    inferClauseType(segment), truncate(segment, MAX_CLAUSE_LENGTH), i + 1);
                Clause savedClause = clauseRepo.save(clause);

                // Evaluate risks against this clause
                List<Risk> clauseRisks = riskService.evaluateClause(savedClause, analysis.getId(), userId);

                if (!clauseRisks.isEmpty()) {
                    savedClause.setHasRisk(true);
                    clauseRepo.save(savedClause);
                    List<Risk> persistedRisks = riskRepo.saveAll(clauseRisks);
                    allRisks.addAll(persistedRisks);
                }

                savedClauses.add(savedClause);
            }

            // ---- 6. Compute composite score and summary ----
            double compositeScore = riskService.computeCompositeRiskScore(allRisks);
            String riskSummary    = riskService.generateRiskSummary(compositeScore, allRisks);

            // ---- 7. Persist final Analysis state ----
            analysis.setStatus(AnalysisStatus.COMPLETED);
            analysis.setCompositeRiskScore(compositeScore);
            analysis.setRiskSummary(riskSummary);
            analysis.setTotalClausesDetected(savedClauses.size());
            analysis.setTotalRisksFound(allRisks.size());
            analysis.setCompletedAt(LocalDateTime.now());
            analysisRepo.save(analysis);

            log.info("Analysis COMPLETED for documentId={}: score={}, risks={}, clauses={}",
                documentId, compositeScore, allRisks.size(), savedClauses.size());

            return buildReport(analysis, savedClauses, allRisks);

        } catch (Exception e) {
            log.error("Analysis pipeline FAILED for documentId={}: {}", documentId, e.getMessage(), e);
            analysis.setStatus(AnalysisStatus.FAILED);
            analysis.setErrorMessage(e.getMessage());
            analysis.setCompletedAt(LocalDateTime.now());
            analysisRepo.save(analysis);
            throw new RuntimeException("Legal analysis pipeline failed: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves the complete analysis report for a document.
     *
     * @param documentId the LegalDocument ID.
     * @param userId     the owner user ID (ownership check).
     * @return the {@link AnalysisReportResponse}.
     * @throws IllegalArgumentException if no analysis exists for this document.
     */
    public AnalysisReportResponse getReport(String documentId, String userId) {
        Analysis analysis = analysisRepo.findByDocumentIdAndUserId(documentId, userId)
            .orElseThrow(() -> new IllegalArgumentException(
                "No analysis found for documentId=" + documentId));

        List<Clause> clauses  = clauseRepo.findByAnalysisIdOrderByClauseIndexAsc(analysis.getId());
        List<Risk>   allRisks = riskRepo.findByAnalysisIdOrderBySeverityDesc(analysis.getId());

        return buildReport(analysis, clauses, allRisks);
    }

    /**
     * Retrieves only HIGH and CRITICAL risk highlights for a document.
     * Used by dashboard summary cards and the AI Advisor module.
     *
     * @param documentId the LegalDocument ID.
     * @param userId     the owner user ID.
     * @return list of high-priority {@link RiskDto} objects.
     */
    public List<RiskDto> getHighPriorityRisks(String documentId, String userId) {
        Analysis analysis = analysisRepo.findByDocumentIdAndUserId(documentId, userId)
            .orElseThrow(() -> new IllegalArgumentException(
                "No analysis found for documentId=" + documentId));

        List<Risk> risks = riskRepo.findByAnalysisIdAndSeverityIn(
            analysis.getId(), List.of(Severity.HIGH, Severity.CRITICAL));

        return risks.stream().map(this::toRiskDto).collect(Collectors.toList());
    }

    /**
     * Lists all analysis summaries for the authenticated user.
     *
     * @param userId the authenticated user ID.
     * @return list of analysis records (no clause/risk detail).
     */
    public List<Analysis> getUserAnalyses(String userId) {
        return analysisRepo.findByUserIdOrderByStartedAtDesc(userId);
    }

    /**
     * Deletes an analysis and all associated clauses and risks.
     *
     * @param documentId the LegalDocument ID.
     * @param userId     the owner user ID.
     */
    public void deleteAnalysis(String documentId, String userId) {
        analysisRepo.findByDocumentIdAndUserId(documentId, userId).ifPresent(analysis -> {
            riskRepo.deleteByAnalysisId(analysis.getId());
            clauseRepo.deleteByAnalysisId(analysis.getId());
            analysisRepo.delete(analysis);
            log.info("Deleted analysis and all associated data for documentId={}", documentId);
        });
    }

    // =========================================================================
    // Internal helpers
    // =========================================================================

    /**
     * Creates and persists a new Analysis record in PENDING state.
     */
    private Analysis createPendingAnalysis(String documentId, String userId) {
        // If an analysis already exists (e.g. re-analysis request), delete the old one first
        analysisRepo.findByDocumentIdAndUserId(documentId, userId).ifPresent(existing -> {
            log.info("Replacing existing analysis for documentId={}", documentId);
            riskRepo.deleteByAnalysisId(existing.getId());
            clauseRepo.deleteByAnalysisId(existing.getId());
            analysisRepo.delete(existing);
        });

        Analysis analysis = new Analysis(documentId, userId);
        return analysisRepo.save(analysis);
    }

    /**
     * Segments raw document text into meaningful clause segments using:
     * 1. Blank-line paragraph splitting (primary).
     * 2. Numbered/lettered clause heading detection (secondary).
     *
     * Segments shorter than {@link #MIN_CLAUSE_LENGTH} characters are discarded.
     */
    List<String> segmentText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        // Step 1: split on blank lines
        String[] paragraphs = PARA_SPLIT.split(text.strip());

        List<String> segments = new ArrayList<>();
        for (String para : paragraphs) {
            String cleaned = para.strip();
            if (cleaned.length() < MIN_CLAUSE_LENGTH) continue;

            // Step 2: if paragraph contains embedded numbered headings, split further
            String[] subSections = CLAUSE_HEADING.split(cleaned);
            if (subSections.length > 1) {
                Arrays.stream(subSections)
                    .map(String::strip)
                    .filter(s -> s.length() >= MIN_CLAUSE_LENGTH)
                    .forEach(segments::add);
            } else {
                segments.add(cleaned);
            }
        }

        return segments;
    }

    /**
     * Heuristically infers the clause type label from a text segment.
     * Uses keyword matching against known clause type signatures.
     */
    String inferClauseType(String text) {
        String lower = text.toLowerCase();

        if (matches(lower, "confidential", "non.?disclosure", "trade secret")) return "CONFIDENTIALITY";
        if (matches(lower, "indemnif", "hold harmless"))                       return "INDEMNIFICATION";
        if (matches(lower, "terminat"))                                        return "TERMINATION";
        if (matches(lower, "non.?compete", "non.?solicitation"))               return "NON_COMPETE";
        if (matches(lower, "governing law", "jurisdiction", "dispute"))        return "GOVERNING_LAW";
        if (matches(lower, "payment", "invoice", "fee", "penalty"))            return "PAYMENT_TERMS";
        if (matches(lower, "renew", "subscription", "auto.renew"))             return "AUTO_RENEWAL";
        if (matches(lower, "liabilit", "limitation", "cap"))                   return "LIABILITY_LIMITATION";
        if (matches(lower, "intellectual property", "ip rights", "license"))   return "IP_AND_LICENSE";
        if (matches(lower, "warranty", "warrant", "represent"))                return "WARRANTIES";
        if (matches(lower, "force majeure", "act of god"))                     return "FORCE_MAJEURE";
        if (matches(lower, "entire agreement", "supersede"))                   return "ENTIRE_AGREEMENT";

        return "GENERAL";
    }

    /**
     * Returns true if the text contains any of the provided regex keywords.
     */
    private boolean matches(String text, String... keywords) {
        for (String kw : keywords) {
            if (Pattern.compile(kw, Pattern.CASE_INSENSITIVE).matcher(text).find()) return true;
        }
        return false;
    }

    /**
     * Truncates a string to maxLen characters.
     */
    private String truncate(String s, int maxLen) {
        return (s != null && s.length() > maxLen) ? s.substring(0, maxLen) + "…" : s;
    }

    // =========================================================================
    // DTO Mappers
    // =========================================================================

    /**
     * Assembles the full {@link AnalysisReportResponse} from persisted entities.
     */
    private AnalysisReportResponse buildReport(Analysis analysis,
                                               List<Clause> clauses,
                                               List<Risk> allRisks) {
        // Build a lookup: clauseId → risks
        var risksByClause = allRisks.stream()
            .collect(Collectors.groupingBy(Risk::getClauseId));

        // Map clauses to DTOs
        List<ClauseDto> clauseDtos = clauses.stream().map(clause -> {
            ClauseDto dto = new ClauseDto();
            dto.setId(clause.getId());
            dto.setClauseType(clause.getClauseType());
            dto.setRawText(clause.getRawText());
            dto.setPageReference(clause.getPageReference());
            dto.setClauseIndex(clause.getClauseIndex());
            dto.setHasRisk(clause.isHasRisk());
            List<Risk> clauseRisks = risksByClause.getOrDefault(clause.getId(), List.of());
            dto.setRisks(clauseRisks.stream().map(this::toRiskDto).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());

        // High-priority risks for the summary panel
        List<RiskDto> highPriorityRisks = allRisks.stream()
            .filter(r -> r.getSeverity() == Severity.HIGH || r.getSeverity() == Severity.CRITICAL)
            .map(this::toRiskDto)
            .collect(Collectors.toList());

        // Severity counts
        long criticalCount = allRisks.stream().filter(r -> r.getSeverity() == Severity.CRITICAL).count();
        long highCount     = allRisks.stream().filter(r -> r.getSeverity() == Severity.HIGH).count();
        long mediumCount   = allRisks.stream().filter(r -> r.getSeverity() == Severity.MEDIUM).count();
        long lowCount      = allRisks.stream().filter(r -> r.getSeverity() == Severity.LOW).count();

        AnalysisReportResponse report = new AnalysisReportResponse();
        report.setAnalysisId(analysis.getId());
        report.setDocumentId(analysis.getDocumentId());
        report.setUserId(analysis.getUserId());
        report.setDocType(analysis.getDocType());
        report.setStatus(analysis.getStatus());
        report.setCompositeRiskScore(analysis.getCompositeRiskScore());
        report.setRiskSummary(analysis.getRiskSummary());
        report.setTotalClausesDetected(analysis.getTotalClausesDetected());
        report.setTotalRisksFound(analysis.getTotalRisksFound());
        report.setCriticalCount((int) criticalCount);
        report.setHighCount((int) highCount);
        report.setMediumCount((int) mediumCount);
        report.setLowCount((int) lowCount);
        report.setClauses(clauseDtos);
        report.setHighPriorityRisks(highPriorityRisks);
        report.setStartedAt(analysis.getStartedAt());
        report.setCompletedAt(analysis.getCompletedAt());

        return report;
    }

    /**
     * Converts a {@link Risk} entity to a {@link RiskDto}.
     */
    private RiskDto toRiskDto(Risk risk) {
        RiskDto dto = new RiskDto();
        dto.setId(risk.getId());
        dto.setClauseId(risk.getClauseId());
        dto.setRuleCode(risk.getRuleCode());
        dto.setRiskTitle(risk.getRiskTitle());
        dto.setSeverity(risk.getSeverity());
        dto.setCitedText(risk.getCitedText());
        dto.setExplanation(risk.getExplanation());
        dto.setSuggestion(risk.getSuggestion());
        return dto;
    }
}
