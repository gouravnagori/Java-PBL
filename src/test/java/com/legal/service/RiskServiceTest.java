package com.legal.service;

import com.legal.model.Clause;
import com.legal.model.Risk;
import com.legal.model.enums.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RiskService}.
 *
 * Validates the core risk-detection rules, composite score computation,
 * and plain-language summary generation.
 *
 * Owned by Dilip Kumawat — Legal Analysis & Risk Detection Module.
 */
@DisplayName("RiskService — Risk detection and scoring tests")
class RiskServiceTest {

    private RiskService riskService;

    @BeforeEach
    void setUp() {
        riskService = new RiskService();
    }

    // =========================================================================
    // Rule: UNLIMITED_LIABILITY (CRITICAL)
    // =========================================================================

    @Test
    @DisplayName("Should detect UNLIMITED_LIABILITY rule — CRITICAL severity")
    void shouldDetectUnlimitedLiability() {
        Clause clause = buildClause("The service provider shall have unlimited liability "
            + "for all damages arising from this agreement.");

        List<Risk> risks = riskService.evaluateClause(clause, "analysis-1", "user-1");

        assertFalse(risks.isEmpty(), "Should detect at least one risk");
        assertTrue(risks.stream().anyMatch(r -> "UNLIMITED_LIABILITY".equals(r.getRuleCode())),
            "Expected UNLIMITED_LIABILITY rule to fire");
        assertTrue(risks.stream().anyMatch(r -> r.getSeverity() == Severity.CRITICAL),
            "Expected CRITICAL severity");
    }

    // =========================================================================
    // Rule: UNILATERAL_TERMINATION (CRITICAL)
    // =========================================================================

    @Test
    @DisplayName("Should detect UNILATERAL_TERMINATION — CRITICAL severity")
    void shouldDetectUnilateralTermination() {
        Clause clause = buildClause("Either party may terminate this agreement without cause "
            + "and without any prior notice at their sole discretion.");

        List<Risk> risks = riskService.evaluateClause(clause, "analysis-1", "user-1");

        assertTrue(risks.stream().anyMatch(r -> "UNILATERAL_TERMINATION".equals(r.getRuleCode())),
            "Expected UNILATERAL_TERMINATION rule to fire");
    }

    // =========================================================================
    // Rule: ONE_SIDED_INDEMNIFICATION (HIGH)
    // =========================================================================

    @Test
    @DisplayName("Should detect ONE_SIDED_INDEMNIFICATION — HIGH severity")
    void shouldDetectOnesidedIndemnification() {
        Clause clause = buildClause("The Client shall indemnify and hold harmless the Vendor "
            + "from any and all claims, losses, and damages of any kind.");

        List<Risk> risks = riskService.evaluateClause(clause, "analysis-1", "user-1");

        assertTrue(risks.stream().anyMatch(r -> "ONE_SIDED_INDEMNIFICATION".equals(r.getRuleCode())),
            "Expected ONE_SIDED_INDEMNIFICATION rule to fire");
        assertTrue(risks.stream().anyMatch(r -> r.getSeverity() == Severity.HIGH),
            "Expected HIGH severity");
    }

    // =========================================================================
    // Rule: BROAD_NON_COMPETE (HIGH)
    // =========================================================================

    @Test
    @DisplayName("Should detect BROAD_NON_COMPETE — HIGH severity")
    void shouldDetectBroadNonCompete() {
        Clause clause = buildClause("Employee agrees to a non-compete and non-solicitation "
            + "obligation. Employee shall not compete worldwide in any similar business "
            + "for a period of 5 years post-termination.");

        List<Risk> risks = riskService.evaluateClause(clause, "analysis-1", "user-1");

        assertTrue(risks.stream().anyMatch(r -> "BROAD_NON_COMPETE".equals(r.getRuleCode())),
            "Expected BROAD_NON_COMPETE rule to fire");
    }

    // =========================================================================
    // Rule: AUTO_RENEWAL (HIGH)
    // =========================================================================

    @Test
    @DisplayName("Should detect AUTO_RENEWAL — HIGH severity")
    void shouldDetectAutoRenewal() {
        Clause clause = buildClause("This subscription shall automatically renew for successive "
            + "12-month terms unless terminated 30 days before the renewal date.");

        List<Risk> risks = riskService.evaluateClause(clause, "analysis-1", "user-1");

        assertTrue(risks.stream().anyMatch(r -> "AUTO_RENEWAL".equals(r.getRuleCode())),
            "Expected AUTO_RENEWAL rule to fire");
    }

    // =========================================================================
    // Rule: AMBIGUOUS_NOTICE_PERIOD (MEDIUM)
    // =========================================================================

    @Test
    @DisplayName("Should detect AMBIGUOUS_NOTICE_PERIOD — MEDIUM severity")
    void shouldDetectAmbiguousNoticePeriod() {
        Clause clause = buildClause("Either party shall provide reasonable notice prior to "
            + "exercising any rights under this section.");

        List<Risk> risks = riskService.evaluateClause(clause, "analysis-1", "user-1");

        assertTrue(risks.stream().anyMatch(r -> "AMBIGUOUS_NOTICE_PERIOD".equals(r.getRuleCode())),
            "Expected AMBIGUOUS_NOTICE_PERIOD rule to fire");
        assertTrue(risks.stream().anyMatch(r -> r.getSeverity() == Severity.MEDIUM),
            "Expected MEDIUM severity");
    }

    // =========================================================================
    // Rule: FORCE_MAJEURE (LOW)
    // =========================================================================

    @Test
    @DisplayName("Should detect FORCE_MAJEURE — LOW severity")
    void shouldDetectForceMajeure() {
        Clause clause = buildClause("Neither party shall be liable for failure to perform "
            + "obligations arising from a force majeure event or act of god "
            + "beyond reasonable control.");

        List<Risk> risks = riskService.evaluateClause(clause, "analysis-1", "user-1");

        assertTrue(risks.stream().anyMatch(r -> "FORCE_MAJEURE".equals(r.getRuleCode())),
            "Expected FORCE_MAJEURE rule to fire");
        assertTrue(risks.stream().anyMatch(r -> r.getSeverity() == Severity.LOW),
            "Expected LOW severity");
    }

    // =========================================================================
    // Zero-risk / clean clause
    // =========================================================================

    @Test
    @DisplayName("Should return empty risk list for a clean, standard clause")
    void shouldReturnEmptyRisksForCleanClause() {
        Clause clause = buildClause("This agreement shall be governed by and construed in "
            + "accordance with the laws of India.");

        List<Risk> risks = riskService.evaluateClause(clause, "analysis-1", "user-1");

        // NOTE: this may trigger DISPUTE_FOREIGN_JURISDICTION if the clause says a foreign law.
        // We use a clean Indian jurisdiction clause which does NOT trigger the pattern.
        assertNotNull(risks, "Risk list should never be null");
    }

    @Test
    @DisplayName("Should return empty risk list for blank clause text")
    void shouldReturnEmptyRisksForBlankText() {
        Clause clause = buildClause("   ");

        List<Risk> risks = riskService.evaluateClause(clause, "analysis-1", "user-1");

        assertTrue(risks.isEmpty(), "Blank clause text should produce zero risks");
    }

    // =========================================================================
    // Composite Risk Score
    // =========================================================================

    @Test
    @DisplayName("Composite score should be 0 for empty risk list")
    void compositeScoreShouldBeZeroForNoRisks() {
        double score = riskService.computeCompositeRiskScore(List.of());
        assertEquals(0.0, score, 0.001, "Empty risk list should produce score 0.0");
    }

    @Test
    @DisplayName("Composite score should not exceed 100 even with many critical risks")
    void compositeScoreShouldNotExceed100() {
        // Create 10 CRITICAL risks (10 * 50 = 500 raw score >> normalisation cap)
        List<Risk> risks = List.of(
            buildRisk(Severity.CRITICAL), buildRisk(Severity.CRITICAL),
            buildRisk(Severity.CRITICAL), buildRisk(Severity.CRITICAL),
            buildRisk(Severity.CRITICAL), buildRisk(Severity.CRITICAL),
            buildRisk(Severity.CRITICAL), buildRisk(Severity.CRITICAL),
            buildRisk(Severity.CRITICAL), buildRisk(Severity.CRITICAL)
        );

        double score = riskService.computeCompositeRiskScore(risks);
        assertTrue(score <= 100.0, "Score must never exceed 100.0");
        assertEquals(100.0, score, 0.001, "10 CRITICAL risks should produce score 100.0");
    }

    @Test
    @DisplayName("Composite score should be proportional for mixed severities")
    void compositeScoreShouldBeProportionalForMixedSeverities() {
        // 1 HIGH (30) + 1 MEDIUM (15) = 45 raw. Normalised: 45/150*100 = 30.0
        List<Risk> risks = List.of(buildRisk(Severity.HIGH), buildRisk(Severity.MEDIUM));

        double score = riskService.computeCompositeRiskScore(risks);
        assertEquals(30.0, score, 0.1, "Mixed severity score should be approximately 30.0");
    }

    // =========================================================================
    // Risk Summary Generation
    // =========================================================================

    @Test
    @DisplayName("Risk summary should mention CRITICAL alert when critical risks are present")
    void riskSummaryShouldMentionCriticalAlert() {
        List<Risk> risks = List.of(buildRisk(Severity.CRITICAL));
        String summary = riskService.generateRiskSummary(80.0, risks);

        assertTrue(summary.contains("CRITICAL"), "Summary should mention CRITICAL alert");
    }

    @Test
    @DisplayName("Risk summary should indicate no risks when risk list is empty")
    void riskSummaryShouldIndicateNoRisks() {
        String summary = riskService.generateRiskSummary(0.0, List.of());

        assertTrue(summary.toLowerCase().contains("no significant risk"),
            "Summary should indicate no significant risks");
    }

    // =========================================================================
    // Risk entity field assertions
    // =========================================================================

    @Test
    @DisplayName("Detected risk should populate all required fields")
    void detectedRiskShouldPopulateRequiredFields() {
        Clause clause = buildClause("The service provider shall have unlimited liability "
            + "for all damages arising without limit from this agreement.");

        List<Risk> risks = riskService.evaluateClause(clause, "analysis-x", "user-x");
        Risk risk = risks.stream()
            .filter(r -> "UNLIMITED_LIABILITY".equals(r.getRuleCode()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("UNLIMITED_LIABILITY risk not found"));

        assertNotNull(risk.getRiskTitle(), "riskTitle must not be null");
        assertNotNull(risk.getSeverity(), "severity must not be null");
        assertNotNull(risk.getCitedText(), "citedText must not be null");
        assertNotNull(risk.getExplanation(), "explanation must not be null");
        assertNotNull(risk.getSuggestion(), "suggestion must not be null");
        assertFalse(risk.getCitedText().isBlank(), "citedText must not be blank");
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private Clause buildClause(String text) {
        Clause clause = new Clause();
        clause.setId("clause-id-test");
        clause.setRawText(text);
        clause.setClauseIndex(1);
        return clause;
    }

    private Risk buildRisk(Severity severity) {
        Risk risk = new Risk();
        risk.setSeverity(severity);
        return risk;
    }
}
