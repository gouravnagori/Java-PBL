package com.legal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legal.model.Risk;
import com.legal.model.enums.DocType;
import com.legal.model.enums.Severity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service that generates simple-language bilingual explanations (English & Hindi)
 * and concrete actionable next steps for legal documents.
 */
@Service
public class LegalAdvisoryService {

    private static final Logger log = LoggerFactory.getLogger(LegalAdvisoryService.class);

    @Value("${groq.api.key:}")
    private String apiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String apiUrl;

    @Value("${groq.api.model:openai/gpt-oss-120b}")
    private String modelName;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LegalAdvisoryService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public static class AdvisoryBundle {
        private final String englishExplanation;
        private final String hindiExplanation;
        private final List<String> nextSteps;
        private final String actionVerdict;

        public AdvisoryBundle(String englishExplanation, String hindiExplanation, List<String> nextSteps, String actionVerdict) {
            this.englishExplanation = englishExplanation;
            this.hindiExplanation = hindiExplanation;
            this.nextSteps = nextSteps;
            this.actionVerdict = actionVerdict;
        }

        public String getEnglishExplanation() { return englishExplanation; }
        public String getHindiExplanation() { return hindiExplanation; }
        public List<String> getNextSteps() { return nextSteps; }
        public String getActionVerdict() { return actionVerdict; }
    }

    /**
     * Synthesizes simple bilingual explanations and next steps for the document.
     */
    public AdvisoryBundle generateAdvisory(DocType docType, String textSample, double compositeScore, List<Risk> risks) {
        // Try LLM dynamic generation first if apiKey is configured
        if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.startsWith("YOUR_")) {
            try {
                AdvisoryBundle aiBundle = queryGroqAdvisory(docType, textSample, compositeScore, risks);
                if (aiBundle != null) {
                    return aiBundle;
                }
            } catch (Exception e) {
                log.warn("Groq LLM advisory synthesis failed, falling back to rule-based generation: {}", e.getMessage());
            }
        }

        // Fast, reliable rule-based bilingual synthesis
        return generateRuleBasedAdvisory(docType, compositeScore, risks);
    }

    private AdvisoryBundle queryGroqAdvisory(DocType docType, String textSample, double compositeScore, List<Risk> risks) throws Exception {
        String truncatedText = textSample.length() > 2500 ? textSample.substring(0, 2500) : textSample;
        String riskListStr = risks.stream()
                .limit(4)
                .map(r -> "- " + r.getRiskTitle() + " (" + r.getSeverity() + "): " + r.getExplanation())
                .collect(Collectors.joining("\n"));

        String prompt = String.format("""
                You are a friendly legal counselor helping a regular person understand their contract.
                Document Type: %s
                Risk Score: %.1f / 100
                Identified Red Flags:
                %s

                Document Text Snippet:
                %s

                Respond in STRICT JSON with four keys:
                1. "englishExplanation": A very simple, easy-to-read explanation (3-5 short sentences, 8th-grade level, NO complex legal jargon). Explain what this contract does, what is expected, and whether it's fair.
                2. "hindiExplanation": A very simple, crystal-clear Hindi explanation (सरल और आसान हिंदी में 3-5 वाक्य). समझाएं कि यह दस्तावेज़ क्या है, इसमें क्या शर्तें हैं और क्या सावधानियां रखनी हैं।
                3. "nextSteps": An array of 4 clear, practical, bullet-point action steps the user must take next before or after signing.
                4. "actionVerdict": One of ["DO NOT SIGN YET — RENEGOTIATE FLAGGED CLAUSES", "PROCEED WITH CAUTION", "SAFE TO SIGN"].

                JSON Output:
                """, docType, compositeScore, riskListStr.isEmpty() ? "None detected" : riskListStr, truncatedText);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", modelName);
        body.put("temperature", 0.3);
        body.put("max_tokens", 1000);
        body.put("messages", List.of(
                Map.of("role", "system", "content", "You are an expert bilingual legal advisor providing plain-language explanations in simple English and natural, easy Hindi."),
                Map.of("role", "user", "content", prompt)
        ));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

        if (response.getBody() != null) {
            JsonNode root = objectMapper.readTree(response.getBody());
            String content = root.path("choices").get(0).path("message").path("content").asText();
            
            // Clean markdown code blocks if wrapped
            if (content.contains("```json")) {
                content = content.substring(content.indexOf("```json") + 7);
                if (content.contains("```")) {
                    content = content.substring(0, content.indexOf("```"));
                }
            } else if (content.contains("```")) {
                content = content.substring(content.indexOf("```") + 3);
                if (content.contains("```")) {
                    content = content.substring(0, content.indexOf("```"));
                }
            }

            JsonNode parsed = objectMapper.readTree(content.trim());
            String eng = parsed.path("englishExplanation").asText();
            String hin = parsed.path("hindiExplanation").asText();
            String verdict = parsed.path("actionVerdict").asText();

            List<String> steps = new ArrayList<>();
            JsonNode stepsNode = parsed.path("nextSteps");
            if (stepsNode.isArray()) {
                for (JsonNode s : stepsNode) {
                    steps.add(s.asText());
                }
            }

            if (!eng.isBlank() && !hin.isBlank() && !steps.isEmpty()) {
                return new AdvisoryBundle(eng, hin, steps, verdict);
            }
        }
        return null;
    }

    private AdvisoryBundle generateRuleBasedAdvisory(DocType docType, double score, List<Risk> risks) {
        String friendlyDocName = formatDocTypeName(docType);

        long criticalCount = risks.stream().filter(r -> r.getSeverity() == Severity.CRITICAL).count();
        long highCount = risks.stream().filter(r -> r.getSeverity() == Severity.HIGH).count();

        // 1. Easy English Explanation
        StringBuilder eng = new StringBuilder();
        eng.append("This document is a ").append(friendlyDocName).append(". ");
        eng.append("In simple words, it sets the official rules, responsibilities, and timeline between both parties. ");

        if (criticalCount > 0) {
            eng.append("Important Warning: Our review detected ").append(criticalCount)
               .append(" high-risk condition(s) (such as unlimited legal liability or one-sided cancellation rules). ");
            eng.append("If you sign without changes, you could be held financially responsible for damages beyond your control. ");
        } else if (highCount > 0) {
            eng.append("Caution: Some terms favor the other party more than you, especially around notice periods or confidentiality limits. ");
        } else {
            eng.append("The overall terms appear balanced, standard, and fair for this type of agreement. ");
        }
        eng.append("Review the key points carefully before putting your signature.");

        // 2. Easy Hindi Explanation (सरल हिंदी)
        StringBuilder hin = new StringBuilder();
        hin.append("यह दस्तावेज़ एक ").append(getHindiDocTypeName(docType)).append(" है। ");
        hin.append("सीधे और आसान शब्दों में कहें तो, यह दोनों पक्षों के बीच काम की ज़िम्मेदारियों, नियमों और शर्तों को तय करता है। ");

        if (criticalCount > 0) {
            hin.append("ज़रूरी चेतावनी: इस अनुबंध में ").append(criticalCount)
               .append(" बड़े जोखिम भरे नियम मिले हैं (जैसे असीमित वित्तीय देनदारी या बिना कारण अनुबंध रद्द करने का अधिकार)। ");
            hin.append("यदि आप इसे बिना बदलाव किए साइन करते हैं, तो किसी भी विवाद की स्थिति में आपको बड़ा नुकसान उठाना पड़ सकता है। ");
        } else if (highCount > 0) {
            hin.append("सावधानी: कुछ शर्तें दूसरी पार्टी के पक्ष में ज्यादा झुकी हुई हैं। खास तौर पर काम खत्म करने के नोटिस और गोपनीयता की अवधि पर ध्यान दें। ");
        } else {
            hin.append("इस दस्तावेज़ की शर्तें आमतौर पर संतुलित और सामान्य मानकों के अनुसार हैं। ");
        }
        hin.append("हस्ताक्षर करने से पहले नीचे दिए गए आवश्यक कदमों का पालन अवश्य करें।");

        // 3. Next Steps Checklist
        List<String> nextSteps = new ArrayList<>();
        if (criticalCount > 0) {
            nextSteps.add("Do Not Sign Immediately: Request the other party to modify the flagged high-risk clauses in writing.");
            nextSteps.add("Cap Your Liability: Ask for a clear cap on damages (e.g., maximum 12 months fees or total contract value).");
            nextSteps.add("Ensure Mutual Notice: Change any sudden termination terms to require at least 30 to 60 days advance written notice.");
            nextSteps.add("Verify Commercials: Double-check payment milestones, tax deductions, and deliverables before signing.");
        } else if (highCount > 0) {
            nextSteps.add("Clarify Notice Periods: Confirm how either party can exit the agreement with reasonable notice.");
            nextSteps.add("Define Confidentiality Duration: Limit secrecy obligations to 2-3 years instead of forever.");
            nextSteps.add("Check Payment Terms: Confirm exact due dates, late fees, and accepted payment methods.");
            nextSteps.add("Save Signed Copy: Ensure both parties receive fully executed and dated copies.");
        } else {
            nextSteps.add("Verify Personal Details: Ensure names, dates, addresses, and compensation match your discussions.");
            nextSteps.add("Check Blank Spaces: Never sign an agreement with empty schedules or placeholder fields.");
            nextSteps.add("Sign & Date: Execute the document and keep a permanent digital and printed copy.");
            nextSteps.add("Calendar Key Milestones: Note down renewal deadlines or deliverable dates.");
        }

        // 4. Action Verdict
        String verdict;
        if (criticalCount > 0) {
            verdict = "DO NOT SIGN YET — RENEGOTIATE FLAGGED CLAUSES";
        } else if (score >= 35 || highCount > 0) {
            verdict = "PROCEED WITH CAUTION — AMEND KEY PROVISIONS";
        } else {
            verdict = "SAFE TO SIGN — STANDARD TERMS VERIFIED";
        }

        return new AdvisoryBundle(eng.toString(), hin.toString(), nextSteps, verdict);
    }

    private String formatDocTypeName(DocType docType) {
        if (docType == null) return "Legal Agreement";
        String[] parts = docType.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) {
                sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }

    private String getHindiDocTypeName(DocType docType) {
        if (docType == null) return "कानूनी समझौता (Legal Agreement)";
        return switch (docType) {
            case NDA -> "गोपनीयता समझौता (Non-Disclosure Agreement / NDA)";
            case EMPLOYMENT -> "रोजगार अनुबंध (Employment Agreement)";
            case RENTAL -> "किराया / लीज समझौता (Rental or Lease Agreement)";
            case SERVICE_AGREEMENT -> "सेवा अनुबंध (Service Agreement)";
            case LOAN -> "ऋण / लोन अनुबंध (Loan Agreement)";
            case PARTNERSHIP -> "साझेदारी अनुबंध (Partnership Agreement)";
            case SAAS -> "सॉफ्टवेयर / सास सेवा अनुबंध (SaaS Agreement)";
            case PURCHASE -> "क्रय / खरीद अनुबंध (Purchase Agreement)";
            case SETTLEMENT -> "समझौता / निपटारा अनुबंध (Settlement Agreement)";
            default -> "कानूनी अनुबंध (Legal Agreement)";
        };
    }
}
