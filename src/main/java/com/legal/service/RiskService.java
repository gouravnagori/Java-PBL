package com.legal.service;

import com.legal.model.Clause;
import com.legal.model.Risk;
import com.legal.model.enums.Severity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule-Based Risk Scoring Engine for legal clause analysis.
 *
 * Each clause is evaluated against a set of deterministic regex rules.
 * A matched rule produces a {@link Risk} entity carrying:
 * - a rule code and title
 * - a severity level (LOW / MEDIUM / HIGH / CRITICAL)
 * - a plain-language explanation
 * - an actionable suggestion for the document recipient
 *
 * This service satisfies:
 * <br>  F7 / FR9 — Risk Detection requirement.
 * <br>  F10 / FR12 — Actionable Suggestions requirement.
 * <br>  F8 / FR10 — Legal Term Explainer (embedded in explanations).
 *
 * Owned by Dilip Kumawat — Legal Analysis & Risk Detection Module.
 */
@Service
public class RiskService {

    private static final Logger log = LoggerFactory.getLogger(RiskService.class);

    // =========================================================================
    // Inner record: a single rule definition
    // =========================================================================

    /**
     * Immutable definition of a single risk-detection rule.
     */
    private record RiskRule(
        String ruleCode,
        String riskTitle,
        Pattern pattern,
        Severity severity,
        String explanation,
        String suggestion
    ) {}

    // =========================================================================
    // Rule Catalogue
    // =========================================================================

    private static final List<RiskRule> RULES = buildRules();

    private static List<RiskRule> buildRules() {
        List<RiskRule> rules = new ArrayList<>();

        // -----------------------------------------------------------------------
        // CRITICAL Rules
        // -----------------------------------------------------------------------
        rules.add(new RiskRule(
            "UNLIMITED_LIABILITY",
            "Unlimited Liability Clause",
            Pattern.compile("(?:unlimited liability|no cap on liability|liable for all damages|"
                + "liable without limit|total liability.*unlimited)", Pattern.CASE_INSENSITIVE),
            Severity.CRITICAL,
            "This clause exposes you to potentially infinite financial liability with no upper limit. "
                + "In a worst-case scenario, you could be held responsible for damages far exceeding "
                + "the contract value.",
            "Negotiate a liability cap, typically set to the total contract value or 12 months of fees paid. "
                + "Ensure mutual caps apply to both parties."
        ));

        rules.add(new RiskRule(
            "UNILATERAL_TERMINATION",
            "Unilateral Termination Without Cause",
            Pattern.compile("(?:terminate.*without cause|terminate.*at will|terminate.*sole discretion"
                + "|immediate termination without|cancel at any time without notice)",
                Pattern.CASE_INSENSITIVE),
            Severity.CRITICAL,
            "The other party can terminate this agreement at any time for any reason without providing "
                + "advance notice. This gives you no protection against sudden contract cancellation.",
            "Request a minimum notice period (30–90 days) and compensation for work completed "
                + "at the time of termination. Push for mutual termination rights."
        ));

        rules.add(new RiskRule(
            "WAIVER_OF_RIGHTS",
            "Blanket Waiver of Legal Rights",
            Pattern.compile("(?:waive.*all rights|waive.*any claim|irrevocably waive|"
                + "waiver of jury trial|waive right to sue)", Pattern.CASE_INSENSITIVE),
            Severity.CRITICAL,
            "You are being asked to permanently give up legal rights, potentially including your right "
                + "to sue or seek legal remedy in court.",
            "Do not sign this clause without legal counsel. At minimum, ensure the waiver is mutual "
                + "and limited in scope to the specific subject matter of this agreement."
        ));

        // -----------------------------------------------------------------------
        // HIGH Rules
        // -----------------------------------------------------------------------
        rules.add(new RiskRule(
            "ONE_SIDED_INDEMNIFICATION",
            "One-Sided Indemnification",
            Pattern.compile("(?:indemnify.*from any.*claim|indemnify.*all.*losses|"
                + "indemnify.*hold harmless.*unlimited|indemnify and defend.*all claims)",
                Pattern.CASE_INSENSITIVE),
            Severity.HIGH,
            "You are required to indemnify (financially protect) the other party against all claims and "
                + "losses, potentially including those caused by their own negligence. This is a heavily "
                + "one-sided obligation.",
            "Request mutual indemnification, and add a carve-out excluding indemnification for the "
                + "other party's own gross negligence or wilful misconduct."
        ));

        rules.add(new RiskRule(
            "BROAD_NON_COMPETE",
            "Overly Broad Non-Compete Clause",
            Pattern.compile("(?:non.?compete|not.*compete.*worldwide|not.*work.*industry"
                + "|non.?solicitation|not.*engage.*similar business)",
                Pattern.CASE_INSENSITIVE),
            Severity.HIGH,
            "A broad non-compete clause may prevent you from working in your field or industry for an "
                + "extended period after this contract ends, significantly limiting your future employment "
                + "or business opportunities.",
            "Negotiate geographical and temporal limits (e.g. 12-month, local area only). Ensure the "
                + "scope is limited to direct competitors and not the entire industry."
        ));

        rules.add(new RiskRule(
            "PERPETUAL_LICENSE",
            "Perpetual and Irrevocable License Grant",
            Pattern.compile("(?:perpetual.*irrevocable.*license|irrevocable.*perpetual.*right"
                + "|permanent license to use|royalty.free.*irrevocable)",
                Pattern.CASE_INSENSITIVE),
            Severity.HIGH,
            "You are granting the other party an irrevocable, permanent right to use your "
                + "intellectual property or data, even if this contract is terminated.",
            "Limit the license grant to the duration of the agreement. Request a clause that "
                + "revokes all licensed rights upon termination."
        ));

        rules.add(new RiskRule(
            "AUTO_RENEWAL",
            "Automatic Renewal Without Notice",
            Pattern.compile("(?:automatically renew|auto.renew|shall renew unless.*notice"
                + "|renewed for successive|unless terminated.*before.*renewal)",
                Pattern.CASE_INSENSITIVE),
            Severity.HIGH,
            "This contract automatically renews unless you actively cancel it before a specific "
                + "deadline. Missing the cancellation window could lock you into another term.",
            "Set a calendar reminder well before the renewal window. Negotiate a longer cancellation "
                + "notice window (60–90 days) and request renewal notifications be sent proactively."
        ));

        // -----------------------------------------------------------------------
        // MEDIUM Rules
        // -----------------------------------------------------------------------
        rules.add(new RiskRule(
            "AMBIGUOUS_NOTICE_PERIOD",
            "Vague or Absent Notice Period",
            Pattern.compile("(?:reasonable notice|as soon as practicable|notice.*may be given"
                + "|sufficient notice|adequate notice)",
                Pattern.CASE_INSENSITIVE),
            Severity.MEDIUM,
            "The notice period is described vaguely ('reasonable', 'sufficient') without a specific "
                + "number of days. This creates ambiguity and potential disputes about whether proper "
                + "notice was given.",
            "Request that all notice periods be defined with explicit calendar days, e.g. '30 calendar days'."
        ));

        rules.add(new RiskRule(
            "DISPUTE_FOREIGN_JURISDICTION",
            "Governing Law in Unfavourable Jurisdiction",
            Pattern.compile("(?:laws of.*delaware|laws of.*cayman|laws of.*singapore"
                + "|exclusive jurisdiction.*foreign|courts of.*[a-z]+ shall have exclusive)",
                Pattern.CASE_INSENSITIVE),
            Severity.MEDIUM,
            "Disputes must be resolved under the laws of a foreign or remote jurisdiction, which may "
                + "require you to engage local legal counsel there and travel for proceedings.",
            "Negotiate for disputes to be resolved in your home jurisdiction or under internationally "
                + "neutral arbitration (e.g. ICC or UNCITRAL rules)."
        ));

        rules.add(new RiskRule(
            "UNILATERAL_AMENDMENT",
            "Unilateral Right to Amend Contract Terms",
            Pattern.compile("(?:may modify.*terms|reserves.*right.*amend|may update.*agreement"
                + "|change.*terms.*without notice|sole discretion.*modify)",
                Pattern.CASE_INSENSITIVE),
            Severity.MEDIUM,
            "The other party retains the right to change the terms of this agreement unilaterally, "
                + "potentially without notifying you or obtaining your consent.",
            "Request that any material amendments require mutual written consent with at least 30 "
                + "days advance written notice."
        ));

        rules.add(new RiskRule(
            "LATE_PAYMENT_PENALTY",
            "High Late Payment Penalty or Interest",
            Pattern.compile("(?:interest.*per.*month|penalty.*late payment|overdue.*charge"
                + "|late fee|interest.*\\d+\\s*%.*per)",
                Pattern.CASE_INSENSITIVE),
            Severity.MEDIUM,
            "The contract imposes financial penalties or high interest rates for late payment. "
                + "Depending on the rate, this could significantly increase your financial exposure.",
            "Verify the interest / penalty rate is reasonable (under 2% per month). "
                + "Negotiate a grace period of at least 7–14 days before penalties accrue."
        ));

        // -----------------------------------------------------------------------
        // LOW Rules
        // -----------------------------------------------------------------------
        rules.add(new RiskRule(
            "ENTIRE_AGREEMENT",
            "Entire Agreement / Integration Clause",
            Pattern.compile("(?:entire agreement|supersedes all prior|constitutes the entire"
                + "|all prior understandings.*merged)",
                Pattern.CASE_INSENSITIVE),
            Severity.LOW,
            "This is a standard integration clause stating that this written contract is the complete "
                + "and final agreement, overriding any prior verbal or written discussions.",
            "Ensure any verbal commitments or email promises made during negotiation are explicitly "
                + "captured in writing within the contract before signing."
        ));

        rules.add(new RiskRule(
            "FORCE_MAJEURE",
            "Force Majeure Clause",
            Pattern.compile("(?:force majeure|act of god|beyond.*reasonable control"
                + "|natural disaster|pandemic.*excuse performance)",
                Pattern.CASE_INSENSITIVE),
            Severity.LOW,
            "A standard force majeure clause suspends obligations if unforeseeable events (natural "
                + "disasters, pandemics) prevent performance. This is generally protective for both "
                + "parties and is considered normal practice.",
            "Ensure the force majeure definition is not overly broad. Confirm there is a mechanism "
                + "to resume performance or terminate the contract if the force majeure extends "
                + "beyond a reasonable period (e.g. 90 days)."
        ));

        rules.add(new RiskRule(
            "CONFIDENTIALITY_STANDARD",
            "Standard Confidentiality Obligation",
            Pattern.compile("(?:shall keep.*confidential|not.*disclose.*third party"
                + "|confidentiality.*obligation|protect.*confidential information)",
                Pattern.CASE_INSENSITIVE),
            Severity.LOW,
            "A standard mutual confidentiality obligation is present. This is typical in most "
                + "commercial agreements and protects trade secrets and proprietary information.",
            "Verify the duration of the confidentiality obligation (typical: 2–5 years post-termination). "
                + "Ensure standard exclusions (publicly available information, prior knowledge) are listed."
        ));

        return rules;
    }

    // =========================================================================
    // Public API
    // =========================================================================

    /**
     * Evaluates a single {@link Clause} against all risk-detection rules.
     *
     * @param clause    the clause entity (not null).
     * @param analysisId the parent analysis ID.
     * @param userId     the owner user ID.
     * @return a (possibly empty) list of {@link Risk} entities detected in this clause.
     */
    public List<Risk> evaluateClause(Clause clause, String analysisId, String userId) {
        List<Risk> detectedRisks = new ArrayList<>();
        String text = clause.getRawText();

        if (text == null || text.isBlank()) {
            return detectedRisks;
        }

        for (RiskRule rule : RULES) {
            Matcher matcher = rule.pattern().matcher(text);
            if (matcher.find()) {
                // Extract the matched snippet as the cited text (max 200 chars for readability)
                String citedText = extractCitation(text, matcher.start(), matcher.end());

                Risk risk = new Risk(
                    clause.getId(),
                    analysisId,
                    userId,
                    rule.ruleCode(),
                    rule.riskTitle(),
                    rule.severity(),
                    citedText,
                    rule.explanation(),
                    rule.suggestion()
                );

                detectedRisks.add(risk);
                log.debug("Rule [{}] triggered in clause index {} — severity: {}",
                    rule.ruleCode(), clause.getClauseIndex(), rule.severity());
            }
        }

        return detectedRisks;
    }

    /**
     * Computes the normalised composite risk score [0, 100] from a list of detected risks.
     *
     * Algorithm:
     * <pre>
     *   rawScore = sum of severity.weight for each risk
     *   compositeScore = min(100, rawScore / normalisationFactor * 100)
     * </pre>
     *
     * The normalisationFactor is calibrated so that a document with three CRITICAL risks
     * reaches the maximum score of 100.
     *
     * @param risks detected risk flags from an analysis run.
     * @return normalised score in [0.0, 100.0].
     */
    public double computeCompositeRiskScore(List<Risk> risks) {
        if (risks == null || risks.isEmpty()) {
            return 0.0;
        }

        int rawScore = risks.stream()
            .mapToInt(r -> r.getSeverity().getWeight())
            .sum();

        // 3 CRITICAL risks (3 * 50 = 150) maps to 100
        final double normalisationFactor = 150.0;
        double compositeScore = (rawScore / normalisationFactor) * 100.0;

        return Math.min(100.0, Math.round(compositeScore * 10.0) / 10.0);
    }

    /**
     * Generates a plain-language risk posture summary based on the composite score
     * and the severity distribution of detected risks.
     *
     * @param compositeScore the normalised 0–100 score.
     * @param risks          all detected risk flags.
     * @return a concise English summary suitable for the risk summary field.
     */
    public String generateRiskSummary(double compositeScore, List<Risk> risks) {
        long criticalCount = risks.stream().filter(r -> r.getSeverity() == Severity.CRITICAL).count();
        long highCount     = risks.stream().filter(r -> r.getSeverity() == Severity.HIGH).count();
        long mediumCount   = risks.stream().filter(r -> r.getSeverity() == Severity.MEDIUM).count();

        if (risks.isEmpty()) {
            return "No significant risk patterns were detected in this document. "
                + "The contract appears to follow standard, balanced terms. "
                + "A qualified legal professional should review the document before signing.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Overall Risk Score: %.1f / 100. ", compositeScore));

        if (criticalCount > 0) {
            sb.append(String.format(
                "⚠️ CRITICAL ALERT: %d critical risk(s) detected — immediate legal review required. ",
                criticalCount));
        }
        if (highCount > 0) {
            sb.append(String.format(
                "%d high-severity clause(s) require negotiation before signing. ", highCount));
        }
        if (mediumCount > 0) {
            sb.append(String.format(
                "%d medium-severity issue(s) present — review ambiguous terms carefully. ", mediumCount));
        }

        sb.append("See individual risk flags below for detailed explanations and suggestions.");
        return sb.toString();
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /**
     * Extracts a readable citation around the matched region (max 200 characters).
     */
    private String extractCitation(String text, int matchStart, int matchEnd) {
        int snippetStart = Math.max(0, matchStart - 40);
        int snippetEnd   = Math.min(text.length(), matchEnd + 80);
        String snippet   = text.substring(snippetStart, snippetEnd).trim();
        return snippet.length() > 200 ? snippet.substring(0, 200) + "…" : snippet;
    }
}
