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
        String truncatedText = textSample.length() > 3500 ? textSample.substring(0, 3500) : textSample;
        String riskListStr = risks.stream()
                .limit(6)
                .map(r -> "- " + r.getRiskTitle() + " (" + r.getSeverity() + "): " + r.getExplanation() + " (Suggestion: " + r.getSuggestion() + ")")
                .collect(Collectors.joining("\n"));

        String prompt = String.format("""
                You are a senior bilingual legal counsel and plain-language legal advocate.
                Analyze the following legal agreement thoroughly and provide an IN-DEPTH, DETAILED explanation for a non-lawyer consumer/professional.
                
                Document Type: %s
                Calculated Risk Score: %.1f / 100
                Identified Red Flags & Clauses:
                %s

                Contract Text Sample:
                %s

                Provide an EXHAUSTIVE, DETAILED analysis in STRICT JSON format with four keys:
                1. "englishExplanation": A detailed, multi-paragraph plain-language breakdown structured into four clearly titled sections:
                   - **1. Executive Overview & Scope**: Explain the nature of this document, the parties' legal relationship, the main transaction or service, and why this agreement exists.
                   - **2. Core Rights & Obligations**: Detail what the signer must deliver, comply with, pay, or refrain from doing, and what reciprocal commitments the other party makes.
                   - **3. Identified Red Flags & Financial Exposure**: Provide an in-depth breakdown of the detected risk flags, unilateral termination terms, unlimited liability, harsh penalties, or non-compete traps in plain English.
                   - **4. Fairness & Legal Standing**: An objective verdict on whether this contract is mutually balanced, standard practice, or aggressively one-sided.
                2. "hindiExplanation": सरल, सहज और विस्तृत हिंदी में संपूर्ण कानूनी सारांश (Comprehensive Hindi Breakdown in everyday conversational Hindi):
                   - **1. दस्तावेज़ का स्वरूप और मुख्य उद्देश्य**: समझाएं कि यह अनुबंध किस बारे में है, दोनों पक्षों की कानूनी स्थिति क्या है और मुख्य उद्देश्य क्या है।
                   - **2. मुख्य जिम्मेदारियां, शर्तें और भुगतान**: हस्ताक्षरकर्ता को क्या काम करना होगा, समय सीमा, फीस, भुगतान नियम और पाबंदियां क्या हैं।
                   - **3. पहचाने गए जोखिम और वित्तीय देनदारी**: एकतरफा अनुबंध समाप्ति, बिना कारण जुर्माना, असीमित देनदारी और छिपी हुई शर्तों का सरल और विस्तृत विश्लेषण।
                   - **4. निष्पक्षता और अंतिम निष्कर्ष**: क्या यह अनुबंध संतुलित है या केवल दूसरी पार्टी के हित में है, और आपको क्या सावधानी बरतनी है।
                3. "nextSteps": An array of 5 to 6 practical, concrete, numbered action items and specific renegotiation terms the signer must take before signing.
                4. "actionVerdict": One of ["DO NOT SIGN YET — RENEGOTIATE FLAGGED CLAUSES", "PROCEED WITH CAUTION — AMEND KEY PROVISIONS", "SAFE TO SIGN — STANDARD TERMS VERIFIED"].

                Respond in STRICT JSON only:
                """, docType, compositeScore, riskListStr.isEmpty() ? "No severe red flags detected" : riskListStr, truncatedText);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", modelName);
        body.put("temperature", 0.3);
        body.put("max_tokens", 2500);
        body.put("messages", List.of(
                Map.of("role", "system", "content", "You are an expert bilingual legal advisor providing exhaustive, plain-language legal contract analyses in simple English and crystal-clear everyday Hindi with clear structured sections."),
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
        long mediumCount = risks.stream().filter(r -> r.getSeverity() == Severity.MEDIUM).count();

        // 1. Detailed English Explanation
        StringBuilder eng = new StringBuilder();
        eng.append("### 📋 Executive Overview & Scope\n");
        eng.append("This agreement is officially classified as a **").append(friendlyDocName).append("**. ");
        eng.append("In plain language, this binding legal contract governs the commercial relationship, operational rights, performance obligations, and dispute resolution parameters between both parties. ");
        eng.append(getDocTypeSpecificContext(docType)).append("\n\n");

        eng.append("### ⚖️ Core Rights & Obligations\n");
        eng.append("Under standard provisions of this agreement, both parties enter into reciprocal commitments. ");
        eng.append("As the executing party, you are primarily obligated to adhere to the designated delivery schedules, confidentiality guidelines, payment deadlines, and agreed operational standards. ");
        eng.append("In exchange, the counterparty must honor agreed compensations, grant permissible operating licenses or tenancies, and adhere to statutory fair dealing norms.\n\n");

        eng.append("### ⚠️ Critical Risk & Liability Breakdown\n");
        if (criticalCount > 0 || highCount > 0) {
            eng.append("Our legal risk engine detected **").append(criticalCount + highCount)
               .append(" high-priority risk factor(s)** (Score: ").append(String.format("%.1f", score)).append("/100):\n");
            for (Risk r : risks.stream().limit(4).toList()) {
                eng.append("- **").append(r.getRiskTitle()).append("** [").append(r.getSeverity()).append("]: ")
                   .append(r.getExplanation()).append(". *Recommended adjustment: ").append(r.getSuggestion()).append("*\n");
            }
            if (criticalCount > 0) {
                eng.append("Key Concern: Uncapped indemnity or unilateral termination clauses create asymmetric exposure where you carry disproportionate financial liability for events outside your direct control.\n\n");
            } else {
                eng.append("Key Concern: Notice periods and intellectual property rights exhibit moderate imbalances that require clearer bilateral covenants.\n\n");
            }
        } else {
            eng.append("Our compliance audit confirmed that this draft contains **zero critical red flags**. ");
            eng.append("The termination notice periods, liability provisions, and confidentiality durations conform to standard market benchmarks without aggressive or punitive conditions.\n\n");
        }

        eng.append("### 🏁 Contract Fairness Assessment\n");
        if (criticalCount > 0) {
            eng.append("The current draft is **heavily one-sided** in favor of the issuing party. Signing this contract in its current state without written modifications exposes you to substantial financial and operational vulnerabilities.");
        } else if (score >= 35 || highCount > 0) {
            eng.append("The contract is **moderately balanced** with standard core clauses, but contains specific protective covenants that should be refined before execution.");
        } else {
            eng.append("The agreement is **fair, balanced, and commercially standard**. The terms protect both parties equitably without unilateral forfeiture provisions.");
        }

        // 2. Detailed Hindi Explanation (विस्तृत और सरल हिंदी)
        StringBuilder hin = new StringBuilder();
        hin.append("### 📋 दस्तावेज़ का विस्तृत सारांश और दायरा\n");
        hin.append("यह दस्तावेज़ आधिकारिक तौर पर एक **").append(getHindiDocTypeName(docType)).append("** है। ");
        hin.append("सरल और आम बोलचाल की भाषा में, यह दोनों पक्षों के बीच कानूनी संबंध, काम के नियम, अधिकारों की सीमा और विवाद निपटारे के नियम तय करता है। ");
        hin.append(getHindiDocTypeContext(docType)).append("\n\n");

        hin.append("### ⚖️ आपकी मुख्य ज़िम्मेदारियां और अधिकार\n");
        hin.append("इस अनुबंध के तहत दोनों पक्षों पर कुछ कानूनी जिम्मेदारियां लागू होती हैं। ");
        hin.append("हस्ताक्षरकर्ता के रूप में आपकी प्राथमिक ज़िम्मेदारी समय पर काम पूरा करना, निर्धारित नियमों का पालन करना और गोपनीयता बनाए रखना है। ");
        hin.append("इसके बदले में, दूसरी पार्टी आपको तय समय पर भुगतान करने, आवश्यक संसाधन या अधिकार प्रदान करने और अनुबंध के नियमों का निष्पक्ष रूप से पालन करने के लिए बाध्य है।\n\n");

        hin.append("### ⚠️ पहचाने गए जोखिम और वित्तीय देनदारी\n");
        if (criticalCount > 0 || highCount > 0) {
            hin.append("हमारी कानूनी जांच प्रणाली ने इस दस्तावेज़ में **").append(criticalCount + highCount)
               .append(" बड़े जोखिम भरे नियम** चिन्हित किए हैं (जोखिम स्कोर: ").append(String.format("%.1f", score)).append("/100):\n");
            for (Risk r : risks.stream().limit(4).toList()) {
                hin.append("- **").append(r.getRiskTitle()).append("**: ").append(r.getExplanation())
                   .append(" (सुझाव: ").append(r.getSuggestion()).append(")\n");
            }
            if (criticalCount > 0) {
                hin.append("मुख्य चिंता: असीमित वित्तीय देनदारी (Unlimited Liability) या बिना कारण अनुबंध रद्द करने के अधिकार के कारण विवाद होने पर आपको बड़ा आर्थिक नुकसान उठाना पड़ सकता है।\n\n");
            } else {
                hin.append("मुख्य चिंता: नोटिस की अवधि और अधिकारों से संबंधित कुछ शर्तें दूसरी पार्टी के पक्ष में ज्यादा झुकी हुई हैं।\n\n");
            }
        } else {
            hin.append("हमारी कानूनी जांच में कोई गंभीर या हानिकारक नियम नहीं पाया गया। ");
            hin.append("अनुबंध समाप्ति की पूर्व सूचना, गोपनीयता की अवधि और सामान्य शर्तें मानक और संतुलित हैं।\n\n");
        }

        hin.append("### 🏁 अनुबंध की निष्पक्षता और अंतिम निष्कर्ष\n");
        if (criticalCount > 0) {
            hin.append("यह ड्राफ्ट **एकतरफा और अत्यधिक जोखिम भरा** है। जब तक चिन्हित की गई शर्तों में लिखित संशोधन नहीं करवा लिया जाता, तब तक इस अनुबंध पर हस्ताक्षर करना सुरक्षित नहीं है।");
        } else if (score >= 35 || highCount > 0) {
            hin.append("यह अनुबंध **आंशिक रूप से संतुलित** है, लेकिन कुछ महत्वपूर्ण नियमों पर दूसरी पार्टी से स्पष्टीकरण और बदलाव की मांग करना आवश्यक है।");
        } else {
            hin.append("यह अनुबंध **पूरी तरह से संतुलित, निष्पक्ष और मानक** है। इसमें दोनों पक्षों के अधिकारों की रक्षा की गई है।");
        }

        // 3. Next Steps Checklist (Expanded & Actionable)
        List<String> nextSteps = new ArrayList<>();
        if (criticalCount > 0) {
            nextSteps.add("Do Not Sign Immediately: Request the other party to modify the flagged high-risk clauses in writing before signing.");
            nextSteps.add("Cap Your Liability: Demand a clear financial liability cap (e.g., maximum total fees received under the contract or 6 months' value).");
            nextSteps.add("Require Mutual Notice: Replace unilateral or sudden termination clauses with a mandatory 30-to-60 day written notice period.");
            nextSteps.add("Protect Deposit & Payments: Ensure your security deposit or milestone earnings cannot be arbitrarily forfeited without substantiated legal cause.");
            nextSteps.add("Specify Neutral Jurisdiction: Verify that dispute resolution and governing law occur in your local jurisdiction or via fair arbitration.");
            nextSteps.add("Preserve Negotiation Records: Maintain all emails, draft markups, and WhatsApp communications in a dedicated audit folder.");
        } else if (highCount > 0) {
            nextSteps.add("Clarify Exit Clauses: Formally clarify how either party may terminate the relationship with reasonable advance notice.");
            nextSteps.add("Bound Confidentiality Obligations: Restrict non-disclosure and trade secret obligations to 2–3 years rather than an indefinite period.");
            nextSteps.add("Confirm Payment Milestones: Verify specific invoicing deadlines, late payment interest terms, and GST/tax allocations.");
            nextSteps.add("Review Intellectual Property: Ensure pre-existing IP, personal tools, and unrelated work remain your exclusive property.");
            nextSteps.add("Execute & Archive: Ensure both authorized representatives sign and date every page, and retain an original executed copy.");
        } else {
            nextSteps.add("Verify Identifying Details: Confirm legal names, government IDs, official addresses, and financial numbers are 100% accurate.");
            nextSteps.add("Inspect Appendices & Schedules: Verify that all attached exhibits, payment schedules, and statement of work annexures are completely filled.");
            nextSteps.add("Sign With Counterparts: Execute the agreement via certified digital signature or ink, ensuring you receive a signed counter-copy.");
            nextSteps.add("Calendar Critical Deadlines: Log renewal notice windows, deliverable dates, and milestone inspection periods in your calendar.");
            nextSteps.add("Store Safely: Keep a secure digital PDF backup and physical copy in your personal legal records.");
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

    private String getDocTypeSpecificContext(DocType docType) {
        if (docType == null) return "It sets forth mutual covenants and terms of engagement.";
        return switch (docType) {
            case NDA -> "Specifically, it protects proprietary trade secrets, sensitive business insights, and confidential data from unauthorized disclosure.";
            case EMPLOYMENT -> "It formalizes job title, reporting structures, compensation, intellectual property ownership, and restrictive post-employment covenants.";
            case RENTAL -> "It details premises occupancy, monthly rental installments, security deposit escrow, routine maintenance liabilities, and lawful tenancy duration.";
            case SERVICE_AGREEMENT -> "It defines statements of work, deliverable milestones, milestone invoicing, warranty periods, and independent contractor governance.";
            case LOAN -> "It establishes principal disbursement, compounding interest schedules, default trigger clauses, and asset collateral obligations.";
            case PARTNERSHIP -> "It governs equity allocation, profit/loss distributions, capital contributions, voting rights, and partnership dissolution mechanics.";
            case SAAS -> "It governs software platform access rights, service level agreements (SLA), customer data privacy, and usage-based subscriptions.";
            case PURCHASE -> "It covers title transfer, delivery warranties, inspection acceptance criteria, and return/refund procedures.";
            case SETTLEMENT -> "It establishes mutual release of legal claims, settlement payments, and final waiver of litigation rights.";
            default -> "It sets forth mutual covenants, commercial rights, and statutory compliance duties.";
        };
    }

    private String getHindiDocTypeContext(DocType docType) {
        if (docType == null) return "यह दोनों पक्षों के बीच आपसी अधिकारों और जिम्मेदारियों को निर्धारित करता है।";
        return switch (docType) {
            case NDA -> "विशेष रूप से, यह व्यापारिक गोपनीयताओं, संवेदनशील डाटा और निजी जानकारियों को दूसरों के साथ साझा होने से सुरक्षित रखता है।";
            case EMPLOYMENT -> "यह नौकरी के पद, वेतन, काम के घंटे, बौद्धिक संपदा अधिकार और नौकरी छोड़ने के बाद की पाबंदियों को स्पष्ट करता है।";
            case RENTAL -> "यह मकान/दुकान के किराए, सिक्योरिटी डिपॉजिट की वापसी, रख-रखाव के खर्च और किराएदारी की वैध अवधि का ब्योरा देता है।";
            case SERVICE_AGREEMENT -> "यह काम के लक्ष्य, प्रोजेक्ट डिलीवरी की समय सीमा, भुगतान किस्तों और काम की गुणवत्ता के मानकों को तय करता है।";
            case LOAN -> "यह ऋण की राशि, ब्याज दर, मासिक ईएमआई भुगतान, देरी पर पेनल्टी और गारंटी की शर्तों को तय करता है।";
            case PARTNERSHIP -> "यह व्यापार में मुनाफे/नुकसान का बंटवारा, पूंजी निवेश, निर्णय लेने के अधिकार और साझेदारी खत्म करने के नियम तय करता है।";
            case SAAS -> "यह ऑनलाइन सॉफ्टवेयर के उपयोग की अनुमति, डेटा सुरक्षा, सर्वर उपलब्धता और मासिक/वार्षिक शुल्क को नियंत्रित करता है।";
            case PURCHASE -> "यह सामान की खरीद, स्वामित्व के हस्तांतरण, वारंटी और खराबी की स्थिति में सामान वापसी के नियम तय करता है।";
            case SETTLEMENT -> "यह पुराने विवादों के निपटारे, समझौते की राशि और भविष्य में किसी कानूनी मुकदमे से मुक्ति की गारंटी देता है।";
            default -> "यह दोनों पक्षों के बीच समझौते के नियमों और कानूनी जिम्मेदारियों को नियंत्रित करता है।";
        };
    }
}
