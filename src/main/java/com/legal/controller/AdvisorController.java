package com.legal.controller;

import com.legal.dto.ApiResponse;
import com.legal.dto.AdvisorRequest;
import com.legal.dto.AdvisorResponse;
import com.legal.model.ChatHistory;
import com.legal.service.AIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for AI Legal Advisor / Q&A.
 * Exposes document-aware Q&A endpoints, context-aware AI explanations, recommendations,
 * disclaimers, and MongoDB chat history management.
 * Owned by Abhishi (AI Legal Advisor / Q&A Module).
 */
@RestController
@RequestMapping("/api/advisor")
@CrossOrigin(origins = "*")
public class AdvisorController {

    private static final Logger log = LoggerFactory.getLogger(AdvisorController.class);

    private final AIService aiService;

    public AdvisorController(AIService aiService) {
        this.aiService = aiService;
    }

    /**
     * Extracts authenticated user ID from security context (if available).
     */
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return auth.getName();
    }

    /**
     * POST /api/advisor/ask
     * Accepts a user question and documentId / documentContext,
     * returns AI explanation, plain-language recommendations, source context, legal disclaimer,
     * and persists entry in MongoDB chat_history.
     */
    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<AdvisorResponse>> askQuestion(@RequestBody AdvisorRequest request) {
        String userId = getCurrentUserId();
        log.info("REST Request: POST /api/advisor/ask for doc: {} by user: {}", request.getDocumentId(), userId);

        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Question cannot be empty."));
        }

        AdvisorResponse response = aiService.askQuestion(request, userId);
        return ResponseEntity.ok(ApiResponse.success("AI Legal Explanation generated successfully", response));
    }

    /**
     * GET /api/advisor/history/{documentId}
     * Retrieves all Q&A history records for a specific legal document.
     */
    @GetMapping("/history/{documentId}")
    public ResponseEntity<ApiResponse<List<ChatHistory>>> getHistoryByDocument(@PathVariable String documentId) {
        String userId = getCurrentUserId();
        log.info("REST Request: GET /api/advisor/history/{} by user: {}", documentId, userId);

        List<ChatHistory> history = aiService.getChatHistory(documentId, userId);
        return ResponseEntity.ok(ApiResponse.success("Chat history retrieved for document", history));
    }

    /**
     * GET /api/advisor/history
     * Retrieves overall chat history for the current user across documents.
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ChatHistory>>> getUserHistory() {
        String userId = getCurrentUserId();
        log.info("REST Request: GET /api/advisor/history for user: {}", userId);

        List<ChatHistory> history = aiService.getUserChatHistory(userId);
        return ResponseEntity.ok(ApiResponse.success("User chat history retrieved", history));
    }

    /**
     * DELETE /api/advisor/history/{documentId}
     * Deletes Q&A chat history for a specific document.
     */
    @DeleteMapping("/history/{documentId}")
    public ResponseEntity<ApiResponse<Void>> deleteHistoryByDocument(@PathVariable String documentId) {
        String userId = getCurrentUserId();
        log.info("REST Request: DELETE /api/advisor/history/{} by user: {}", documentId, userId);

        aiService.clearChatHistory(documentId, userId);
        return ResponseEntity.ok(ApiResponse.success("Chat history deleted successfully", null));
    }

    /**
     * GET /api/advisor/health
     * Health check endpoint for AI Legal Advisor / Q&A Module.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "module", "AI Legal Advisor / Q&A",
                "lead", "Abhishi",
                "status", "UP",
                "features", List.of("Document-Aware Q&A", "Plain-Language Explanations", "AI Provider Integration", "Chat History", "Legal Disclaimer"),
                "endpoints", List.of("POST /api/advisor/ask", "GET /api/advisor/history/{documentId}", "GET /api/advisor/history")
        ));
    }
}
