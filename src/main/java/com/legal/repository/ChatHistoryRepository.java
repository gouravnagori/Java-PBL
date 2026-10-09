package com.legal.repository;

import com.legal.model.ChatHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Chat History persistence in MongoDB.
 * Owned and managed by Abhishi (AI Legal Advisor / Q&A Module).
 */
@Repository
public interface ChatHistoryRepository extends MongoRepository<ChatHistory, String> {

    /**
     * Retrieve all Q&A history entries for a specific document ordered by timestamp.
     */
    List<ChatHistory> findByDocumentIdOrderByCreatedAtAsc(String documentId);

    /**
     * Retrieve Q&A history entries for a user and specific document.
     */
    List<ChatHistory> findByUserIdAndDocumentIdOrderByCreatedAtAsc(String userId, String documentId);

    /**
     * Retrieve all Q&A history entries for an authenticated user across all documents.
     */
    List<ChatHistory> findByUserIdOrderByCreatedAtDesc(String userId);

    /**
     * Delete chat history entries associated with a document and user.
     */
    void deleteByDocumentIdAndUserId(String documentId, String userId);

    /**
     * Delete chat history entries for a document.
     */
    void deleteByDocumentId(String documentId);
}
