package com.legal.repository;

import com.legal.model.DocumentStatus;
import com.legal.model.LegalDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB Repository for Legal Documents.
 * Module: Document Management & Processing (Gourav Nagori)
 */
@Repository
public interface DocumentRepository extends MongoRepository<LegalDocument, String> {

    /** Find all documents belonging to a specific user ordered by upload date descending */
    List<LegalDocument> findByUserIdOrderByUploadedAtDesc(String userId);

    /** Find documents by current processing lifecycle status */
    List<LegalDocument> findByStatus(DocumentStatus status);

    /** Find a document ensuring it belongs to the authenticated user (multi-tenant protection) */
    Optional<LegalDocument> findByIdAndUserId(String id, String userId);

    /** Query document by SHA-256 file checksum to detect duplicates */
    @Query("{ 'metadata.fileHash': ?0 }")
    Optional<LegalDocument> findByFileHash(String fileHash);

    /** Check if document with matching file hash already exists */
    @Query(value = "{ 'metadata.fileHash': ?0 }", exists = true)
    boolean existsByFileHash(String fileHash);

    /** Count documents owned by a user */
    long countByUserId(String userId);

    /** Count documents currently in a specific processing status */
    long countByStatus(DocumentStatus status);
}
