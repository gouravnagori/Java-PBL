package com.legal.service;

import com.legal.dto.AdvisorRequest;
import com.legal.dto.AdvisorResponse;
import com.legal.model.ChatHistory;

import java.util.List;

/**
 * Service interface for AI Legal Advisor / Q&A operations.
 * Defines contract for processing document-aware questions, AI integration, and chat history.
 * Owned by Abhishi (AI Legal Advisor / Q&A Module).
 */
public interface AIService {

    /**
     * Processes user question with relevant document context and returns AI explanation,
     * plain-language recommendations, source context, and legal disclaimer.
     */
    AdvisorResponse askQuestion(AdvisorRequest request, String userId);

    /**
     * Retrieves Q&A / Chat history for a specific document.
     */
    List<ChatHistory> getChatHistory(String documentId, String userId);

    /**
     * Retrieves overall chat history for a specific user.
     */
    List<ChatHistory> getUserChatHistory(String userId);

    /**
     * Clears chat history entries for a given document.
     */
    void clearChatHistory(String documentId, String userId);
}
