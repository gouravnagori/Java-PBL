package com.legal.service;

import com.legal.model.enums.DocType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Legal Document Classification Service.
 *
 * Applies regex keyword heuristics to infer the legal document category (DocType)
 * from raw extracted text. This satisfies:
 * <br>  F4 / FR6 — Document Classification requirement.
 *
 * Strategy:
 * - Each DocType is associated with a Pattern of high-signal keywords / phrases.
 * - The document text is lowercased and matched against each pattern.
 * - The DocType whose pattern produces the highest match count wins.
 * - Ties default to GENERAL.
 *
 * Owned by Dilip Kumawat — Legal Analysis & Risk Detection Module.
 */
@Service
public class ClassificationService {

    private static final Logger log = LoggerFactory.getLogger(ClassificationService.class);

    /**
     * Maps each DocType to a compiled regex pattern of discriminating keywords.
     * Patterns use non-capturing groups for efficiency.
     */
    private static final Map<DocType, Pattern> CLASSIFICATION_PATTERNS = Map.of(

        DocType.NDA, Pattern.compile(
            "(?:non.?disclosure|confidentiality agreement|nda|trade secret|proprietary information" +
            "|shall not disclose|confidential information)", Pattern.CASE_INSENSITIVE),

        DocType.EMPLOYMENT, Pattern.compile(
            "(?:employment agreement|offer of employment|salary|wages|job title" +
            "|probationary period|employee benefits|employer|termination of employment" +
            "|annual leave|notice period)", Pattern.CASE_INSENSITIVE),

        DocType.RENTAL, Pattern.compile(
            "(?:rental agreement|lease agreement|landlord|tenant|rent|security deposit" +
            "|eviction|premises|monthly rent|tenancy)", Pattern.CASE_INSENSITIVE),

        DocType.LOAN, Pattern.compile(
            "(?:loan agreement|borrower|lender|principal amount|interest rate|repayment" +
            "|collateral|promissory note|credit facility|default on loan)", Pattern.CASE_INSENSITIVE),

        DocType.SERVICE_AGREEMENT, Pattern.compile(
            "(?:service agreement|scope of work|deliverables|professional services" +
            "|service provider|client|milestone|service level|sla|statement of work)",
            Pattern.CASE_INSENSITIVE),

        DocType.SAAS, Pattern.compile(
            "(?:software as a service|saas|subscription|license key|api access" +
            "|cloud service|uptime|data processing agreement|dpa|acceptable use)",
            Pattern.CASE_INSENSITIVE),

        DocType.PARTNERSHIP, Pattern.compile(
            "(?:partnership agreement|joint venture|profit sharing|partner|co-founder" +
            "|equity|capital contribution|dissolution of partnership)",
            Pattern.CASE_INSENSITIVE),

        DocType.PURCHASE, Pattern.compile(
            "(?:purchase agreement|sale of goods|buyer|seller|invoice|delivery|warranty" +
            "|title of goods|purchase price|bill of sale)", Pattern.CASE_INSENSITIVE),

        DocType.SETTLEMENT, Pattern.compile(
            "(?:settlement agreement|release of claims|full and final settlement" +
            "|waiver of rights|mutual release|dispute resolution|without admission)",
            Pattern.CASE_INSENSITIVE)
    );

    /**
     * Classifies the legal document text into the most probable {@link DocType}.
     *
     * @param documentText raw extracted text of the legal document (not null).
     * @return the inferred {@link DocType}; defaults to {@link DocType#GENERAL} when
     *         no pattern produces sufficient confidence.
     */
    public DocType classify(String documentText) {
        if (documentText == null || documentText.isBlank()) {
            log.warn("ClassificationService received blank document text; defaulting to GENERAL.");
            return DocType.GENERAL;
        }

        String text = documentText.toLowerCase(Locale.ROOT);
        DocType bestMatch = DocType.GENERAL;
        int highestScore = 0;

        for (Map.Entry<DocType, Pattern> entry : CLASSIFICATION_PATTERNS.entrySet()) {
            long matchCount = entry.getValue().matcher(text).results().count();
            if (matchCount > highestScore) {
                highestScore = (int) matchCount;
                bestMatch = entry.getKey();
            }
        }

        log.info("Document classified as {} with {} keyword matches.", bestMatch, highestScore);
        return bestMatch;
    }
}
