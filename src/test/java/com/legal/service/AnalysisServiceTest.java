package com.legal.service;

import com.legal.dto.AnalysisReportResponse;
import com.legal.dto.RiskDto;
import com.legal.model.Analysis;
import com.legal.model.Clause;
import com.legal.model.Risk;
import com.legal.model.enums.AnalysisStatus;
import com.legal.model.enums.DocType;
import com.legal.model.enums.Severity;
import com.legal.repository.AnalysisResultRepository;
import com.legal.repository.ClauseRepository;
import com.legal.repository.RiskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AnalysisService} using Mockito.
 *
 * Tests the orchestration logic, clause segmentation, and persistence interactions
 * without requiring a live MongoDB connection.
 *
 * Owned by Dilip Kumawat — Legal Analysis & Risk Detection Module.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AnalysisService — Orchestration and pipeline tests")
class AnalysisServiceTest {

    @Mock private AnalysisResultRepository analysisRepo;
    @Mock private ClauseRepository clauseRepo;
    @Mock private RiskRepository riskRepo;
    @Mock private ClassificationService classificationService;
    @Mock private RiskService riskService;
    @Mock private LegalAdvisoryService legalAdvisoryService;

    @InjectMocks
    private AnalysisService analysisService;

    private static final String DOC_ID  = "doc-001";
    private static final String USER_ID = "user-001";
    private static final String ANALYSIS_ID = "analysis-001";

    // =========================================================================
    // runAnalysis — happy path
    // =========================================================================

    @Test
    @DisplayName("runAnalysis should complete successfully and persist entities")
    void runAnalysisShouldCompleteSuccessfully() {
        // ---- Arrange ----
        String text = "This Non-Disclosure Agreement is entered into between parties.\n\n"
            + "The employee shall not disclose confidential information.\n\n"
            + "The party shall have unlimited liability for all damages.";

        // No existing analysis
        when(analysisRepo.findByDocumentIdAndUserId(DOC_ID, USER_ID)).thenReturn(Optional.empty());

        // Mock save to return the analysis with an ID set
        Analysis savedAnalysis = new Analysis(DOC_ID, USER_ID);
        savedAnalysis.setId(ANALYSIS_ID);
        when(analysisRepo.save(any(Analysis.class))).thenReturn(savedAnalysis);

        // ClassificationService mock
        when(classificationService.classify(anyString())).thenReturn(DocType.NDA);

        // Clause repository mock
        Clause savedClause = new Clause(ANALYSIS_ID, USER_ID, "CONFIDENTIALITY", "sample text", 1);
        savedClause.setId("clause-001");
        when(clauseRepo.save(any(Clause.class))).thenReturn(savedClause);

        // RiskService mocks — return one critical risk
        Risk mockRisk = new Risk("clause-001", ANALYSIS_ID, USER_ID,
            "UNLIMITED_LIABILITY", "Unlimited Liability", Severity.CRITICAL,
            "unlimited liability", "This is risky.", "Negotiate a cap.");
        mockRisk.setId("risk-001");
        when(riskService.evaluateClause(any(Clause.class), eq(ANALYSIS_ID), eq(USER_ID)))
            .thenReturn(List.of(mockRisk));
        when(riskRepo.saveAll(anyList())).thenReturn(List.of(mockRisk));
        when(riskService.computeCompositeRiskScore(anyList())).thenReturn(75.0);
        when(riskService.generateRiskSummary(anyDouble(), anyList())).thenReturn("High risk document.");

        // ---- Act ----
        AnalysisReportResponse report = analysisService.runAnalysis(DOC_ID, text, USER_ID);

        // ---- Assert ----
        assertNotNull(report, "Report must not be null");
        assertEquals(DocType.NDA, report.getDocType(), "DocType should be NDA");
        assertEquals(AnalysisStatus.COMPLETED, report.getStatus(), "Status should be COMPLETED");

        // Verify repository interactions
        verify(analysisRepo, atLeast(2)).save(any(Analysis.class)); // PENDING + COMPLETED saves
        verify(clauseRepo, atLeast(1)).save(any(Clause.class));
        verify(riskRepo, atLeast(1)).saveAll(anyList());
    }

    // =========================================================================
    // Clause Segmentation
    // =========================================================================

    @Test
    @DisplayName("segmentText should correctly split paragraphs on blank lines")
    void segmentTextShouldSplitOnBlankLines() {
        String text = "First clause about confidentiality obligations of the receiving party.\n\n"
            + "Second clause about indemnification and hold harmless provisions.\n\n"
            + "Third clause defining the governing law and dispute jurisdiction.";

        List<String> segments = analysisService.segmentText(text);

        assertEquals(3, segments.size(), "Should produce 3 segments from 3 paragraphs");
    }

    @Test
    @DisplayName("segmentText should discard segments shorter than minimum length")
    void segmentTextShouldDiscardShortSegments() {
        String text = "A.\n\nThis is a proper long clause about confidentiality obligations and requirements.\n\nB.";

        List<String> segments = analysisService.segmentText(text);

        // "A." and "B." should be too short to be kept; only the long clause remains
        assertEquals(1, segments.size(), "Short stub segments should be discarded");
    }

    @Test
    @DisplayName("segmentText should return empty list for blank text")
    void segmentTextShouldReturnEmptyForBlankText() {
        List<String> segments = analysisService.segmentText("   ");
        assertTrue(segments.isEmpty(), "Blank text should produce no segments");
    }

    @Test
    @DisplayName("segmentText should return empty list for null text")
    void segmentTextShouldReturnEmptyForNullText() {
        List<String> segments = analysisService.segmentText(null);
        assertTrue(segments.isEmpty(), "Null text should produce no segments");
    }

    // =========================================================================
    // Clause Type Inference
    // =========================================================================

    @Test
    @DisplayName("inferClauseType should correctly identify CONFIDENTIALITY")
    void shouldInferConfidentialityClauseType() {
        String text = "The receiving party agrees to keep all confidential information secret "
            + "and not to disclose trade secrets to any third party.";
        assertEquals("CONFIDENTIALITY", analysisService.inferClauseType(text));
    }

    @Test
    @DisplayName("inferClauseType should correctly identify TERMINATION")
    void shouldInferTerminationClauseType() {
        String text = "Either party may terminate this agreement with 30 days notice. "
            + "Upon termination all obligations shall cease.";
        assertEquals("TERMINATION", analysisService.inferClauseType(text));
    }

    @Test
    @DisplayName("inferClauseType should correctly identify PAYMENT_TERMS")
    void shouldInferPaymentTermsClauseType() {
        String text = "The client shall pay the invoice within 30 days. Late payment fee "
            + "of 2% per month shall apply to overdue payments.";
        assertEquals("PAYMENT_TERMS", analysisService.inferClauseType(text));
    }

    @Test
    @DisplayName("inferClauseType should return GENERAL for unrecognised clause")
    void shouldReturnGeneralForUnrecognisedClause() {
        String text = "This clause contains miscellaneous provisions that do not fall "
            + "into any standard legal category.";
        assertEquals("GENERAL", analysisService.inferClauseType(text));
    }

    // =========================================================================
    // getReport — existing analysis retrieval
    // =========================================================================

    @Test
    @DisplayName("getReport should throw IllegalArgumentException when no analysis found")
    void getReportShouldThrowWhenNoAnalysisExists() {
        when(analysisRepo.findByDocumentIdAndUserId(DOC_ID, USER_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> analysisService.getReport(DOC_ID, USER_ID),
            "Should throw when analysis does not exist for document");
    }

    @Test
    @DisplayName("getReport should return completed report when analysis exists")
    void getReportShouldReturnCompletedReport() {
        Analysis analysis = new Analysis(DOC_ID, USER_ID);
        analysis.setId(ANALYSIS_ID);
        analysis.setStatus(AnalysisStatus.COMPLETED);
        analysis.setDocType(DocType.NDA);
        analysis.setCompositeRiskScore(55.0);
        analysis.setRiskSummary("Moderate risk.");

        when(analysisRepo.findByDocumentIdAndUserId(DOC_ID, USER_ID)).thenReturn(Optional.of(analysis));
        when(clauseRepo.findByAnalysisIdOrderByClauseIndexAsc(ANALYSIS_ID)).thenReturn(List.of());
        when(riskRepo.findByAnalysisIdOrderBySeverityDesc(ANALYSIS_ID)).thenReturn(List.of());

        AnalysisReportResponse report = analysisService.getReport(DOC_ID, USER_ID);

        assertNotNull(report);
        assertEquals(AnalysisStatus.COMPLETED, report.getStatus());
        assertEquals(55.0, report.getCompositeRiskScore(), 0.01);
        assertEquals(DocType.NDA, report.getDocType());
    }

    // =========================================================================
    // getHighPriorityRisks
    // =========================================================================

    @Test
    @DisplayName("getHighPriorityRisks should return only HIGH and CRITICAL risks")
    void getHighPriorityRisksShouldFilterCorrectly() {
        Analysis analysis = new Analysis(DOC_ID, USER_ID);
        analysis.setId(ANALYSIS_ID);
        when(analysisRepo.findByDocumentIdAndUserId(DOC_ID, USER_ID)).thenReturn(Optional.of(analysis));

        Risk criticalRisk = new Risk("c1", ANALYSIS_ID, USER_ID, "UNLIMITED_LIABILITY",
            "Unlimited Liability", Severity.CRITICAL, "unlimited liability",
            "Explanation.", "Suggestion.");
        criticalRisk.setId("r1");
        Risk highRisk = new Risk("c2", ANALYSIS_ID, USER_ID, "ONE_SIDED_INDEMNIFICATION",
            "One-Sided Indemnification", Severity.HIGH, "indemnify all losses",
            "Explanation.", "Suggestion.");
        highRisk.setId("r2");

        when(riskRepo.findByAnalysisIdAndSeverityIn(eq(ANALYSIS_ID), anyList()))
            .thenReturn(List.of(criticalRisk, highRisk));

        List<RiskDto> highPriorityRisks = analysisService.getHighPriorityRisks(DOC_ID, USER_ID);

        assertEquals(2, highPriorityRisks.size(), "Should return both HIGH and CRITICAL risks");
        assertTrue(highPriorityRisks.stream().allMatch(
            r -> r.getSeverity() == Severity.HIGH || r.getSeverity() == Severity.CRITICAL),
            "All returned risks should be HIGH or CRITICAL");
    }

    // =========================================================================
    // deleteAnalysis
    // =========================================================================

    @Test
    @DisplayName("deleteAnalysis should cascade delete clauses and risks")
    void deleteAnalysisShouldCascadeDelete() {
        Analysis analysis = new Analysis(DOC_ID, USER_ID);
        analysis.setId(ANALYSIS_ID);
        when(analysisRepo.findByDocumentIdAndUserId(DOC_ID, USER_ID)).thenReturn(Optional.of(analysis));

        analysisService.deleteAnalysis(DOC_ID, USER_ID);

        verify(riskRepo).deleteByAnalysisId(ANALYSIS_ID);
        verify(clauseRepo).deleteByAnalysisId(ANALYSIS_ID);
        verify(analysisRepo).delete(analysis);
    }

    @Test
    @DisplayName("deleteAnalysis should do nothing when no analysis exists")
    void deleteAnalysisShouldBeNoopWhenNotFound() {
        when(analysisRepo.findByDocumentIdAndUserId(DOC_ID, USER_ID)).thenReturn(Optional.empty());

        analysisService.deleteAnalysis(DOC_ID, USER_ID);

        verify(riskRepo, never()).deleteByAnalysisId(any());
        verify(clauseRepo, never()).deleteByAnalysisId(any());
        verify(analysisRepo, never()).delete(any(Analysis.class));
    }
}
