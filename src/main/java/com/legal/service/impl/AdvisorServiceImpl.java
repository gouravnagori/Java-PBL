package com.legal.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legal.dto.AdvisorRequest;
import com.legal.dto.AdvisorResponse;
import com.legal.model.ChatHistory;
import com.legal.model.LegalDocument;
import com.legal.repository.ChatHistoryRepository;
import com.legal.repository.DocumentRepository;
import com.legal.service.AIService;
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

/**
 * Service implementation for AI Legal Advisor / Q&A.
 * Connects AI provider (Groq / OpenAI API), passes document context, formats explanations,
 * handles AI timeouts & safe fallbacks, and persists chat history to MongoDB.
 * Managed by Abhishi (AI Legal Advisor / Q&A Module).
 */
@Service
public class AdvisorServiceImpl implements AIService {

    private static final Logger log = LoggerFactory.getLogger(AdvisorServiceImpl.class);

    @Value("${groq.api.key:}")
    private String apiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String apiUrl;

    @Value("${groq.api.model:llama-3.3-70b-versatile}")
    private String modelName;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final DocumentRepository documentRepository;
    private final ChatHistoryRepository chatHistoryRepository;

    private static final String SYSTEM_PROMPT = """
            You are an expert AI Legal Advisor for Indian Legal Documents.
            Your purpose is to assist citizens in understanding legal contracts, notices, summons, and agreements in plain language.

            INSTRUCTIONS:
            1. Answer the user's specific question based directly on the provided legal document context.
            2. Break down complex legal jargon into simple, clear explanations.
            3. Highlight relevant clauses, obligations, penalties, or constitutional rights.
            4. Provide actionable, practical next steps for the user.
            5. Maintain a professional, empathetic, and objective tone.
            6. Include an explicit reference to the exact text snippet or clause in the document that supports your explanation.

            IMPORTANT: Provide structured responses with headings for:
            - **Direct Answer**
            - **Plain-Language Explanation**
            - **Key Clauses & Context**
            - **Actionable Recommendations**
            """;

    public AdvisorServiceImpl(RestTemplate restTemplate,
                              ObjectMapper objectMapper,
                              DocumentRepository documentRepository,
                              ChatHistoryRepository chatHistoryRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.documentRepository = documentRepository;
        this.chatHistoryRepository = chatHistoryRepository;
    }

    @Override
    public AdvisorResponse askQuestion(AdvisorRequest request, String userId) {
        if (request == null || request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            throw new IllegalArgumentException("Question cannot be empty.");
        }

        String question = request.getQuestion().trim();
        String documentId = request.getDocumentId();
        String contextText = extractDocumentContext(request);

        log.info("Processing AI Legal Advisor Q&A request for documentId: {} by user: {}", documentId, userId);

        String aiAnswer;
        String sourceContextSnippet = extractSourceSnippet(contextText, question);

        try {
            if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.contains("YOUR_KEY")) {
                aiAnswer = callLLMService(question, contextText);
            } else {
                log.warn("Groq API key not configured. Using rule-based fallback advisor engine.");
                aiAnswer = generateFallbackExplanation(question, contextText);
            }
        } catch (Exception e) {
            log.error("AI service communication failed/timed out: {}. Invoking safe fallback handler.", e.getMessage());
            aiAnswer = generateFallbackExplanation(question, contextText);
        }

        AdvisorResponse response = new AdvisorResponse();
        response.setDocumentId(documentId);
        response.setQuestion(question);
        response.setAnswer(aiAnswer);
        response.setSourceContext(sourceContextSnippet);
        response.setDisclaimer(AdvisorResponse.DEFAULT_DISCLAIMER);

        // Save interaction to chat_history collection in MongoDB
        try {
            ChatHistory historyEntry = new ChatHistory(
                    userId,
                    documentId,
                    question,
                    aiAnswer,
                    AdvisorResponse.DEFAULT_DISCLAIMER,
                    sourceContextSnippet
            );
            ChatHistory saved = chatHistoryRepository.save(historyEntry);
            response.setId(saved.getId());
            log.info("Successfully persisted chat history record ID: {} in MongoDB", saved.getId());
        } catch (Exception dbEx) {
            log.warn("Failed to persist chat history to MongoDB: {}", dbEx.getMessage());
        }

        return response;
    }

    @Override
    public List<ChatHistory> getChatHistory(String documentId, String userId) {
        if (documentId == null || documentId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        if (userId != null && !userId.trim().isEmpty()) {
            return chatHistoryRepository.findByUserIdAndDocumentIdOrderByCreatedAtAsc(userId, documentId);
        }
        return chatHistoryRepository.findByDocumentIdOrderByCreatedAtAsc(documentId);
    }

    @Override
    public List<ChatHistory> getUserChatHistory(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return chatHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public void clearChatHistory(String documentId, String userId) {
        if (documentId != null && !documentId.trim().isEmpty()) {
            if (userId != null && !userId.trim().isEmpty()) {
                chatHistoryRepository.deleteByDocumentIdAndUserId(documentId, userId);
            } else {
                chatHistoryRepository.deleteByDocumentId(documentId);
            }
        }
    }

    private String extractDocumentContext(AdvisorRequest request) {
        if (request.getDocumentContext() != null && !request.getDocumentContext().trim().isEmpty()) {
            return request.getDocumentContext().trim();
        }
        if (request.getDocumentId() != null && !request.getDocumentId().trim().isEmpty()) {
            Optional<LegalDocument> docOpt = documentRepository.findById(request.getDocumentId());
            if (docOpt.isPresent() && docOpt.get().getExtractedText() != null) {
                return docOpt.get().getExtractedText();
            }
        }
        return "No specific document text provided. Answering based on general Indian legal standards.";
    }

    private String callLLMService(String question, String context) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey.trim());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelName);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
        messages.add(Map.of("role", "user", "content", "DOCUMENT CONTEXT:\n" + context + "\n\nUSER QUESTION:\n" + question));

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.3);
        requestBody.put("max_tokens", 1024);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

        if (response.getBody() != null) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode choices = root.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                return choices.get(0).path("message").path("content").asText();
            }
        }
        throw new RuntimeException("Empty choice payload returned from LLM provider");
    }

    /**
     * Rule-based safe fallback handler for Day 4 requirements (error/timeout handling).
     */
    private String generateFallbackExplanation(String question, String context) {
        String lowerQ = question.toLowerCase();
        StringBuilder sb = new StringBuilder();

        sb.append("### **Direct Answer**\n");
        if (lowerQ.contains("risk") || lowerQ.contains("penalty") || lowerQ.contains("fine")) {
            sb.append("Based on standard Indian legal framework, contracts and documents carrying penalty clauses require clear mutual consent. ")
              .append("Any unilateral penalty without notice may be challenged under Section 73/74 of the Indian Contract Act, 1872.\n\n");
        } else if (lowerQ.contains("rights") || lowerQ.contains("right") || lowerQ.contains("protect")) {
            sb.append("As an Indian citizen, you possess fundamental constitutional rights under Article 14 (Equality) and Article 21 (Protection of Life & Personal Liberty), ")
              .append("as well as statutory rights guaranteeing transparent contract disclosures.\n\n");
        } else if (lowerQ.contains("notice") || lowerQ.contains("terminate") || lowerQ.contains("cancel")) {
            sb.append("Standard legal termination typically mandates a written notice period (usually 15-30 days) prior to contract cancellation.\n\n");
        } else {
            sb.append("Analysis of your question regarding this legal document indicates important obligations and conditions to take note of.\n\n");
        }

        sb.append("### **Plain-Language Explanation**\n")
          .append("The document context specifies rights and responsibilities binding both parties. ")
          .append("Ensure that all terms related to timelines, financial commitments, and dispute resolution venues are agreed upon in writing.\n\n");

        sb.append("### **Key Clauses & Context**\n");
        if (context != null && context.length() > 5) {
            String snippet = context.length() > 300 ? context.substring(0, 300) + "..." : context;
            sb.append("> \"").append(snippet).append("\"\n\n");
        } else {
            sb.append("No extracted document text was attached to this query.\n\n");
        }

        sb.append("### **Actionable Recommendations**\n")
          .append("1. **Verify Signatures:** Do not sign any amendment without prior legal review.\n")
          .append("2. **Document Evidence:** Retain dated digital and physical copies of all notices.\n")
          .append("3. **Legal Aid Contact:** For free legal assistance under NALSA, call National Legal Services Helpline **15100**.");

        return sb.toString();
    }

    private String extractSourceSnippet(String context, String question) {
        if (context == null || context.trim().isEmpty() || context.startsWith("No specific document")) {
            return "General Legal Context";
        }
        String[] lines = context.split("\n");
        for (String line : lines) {
            if (line.trim().length() > 20) {
                return line.trim();
            }
        }
        return context.length() > 150 ? context.substring(0, 150) + "..." : context;
    }
}
