package com.legal.service.extractor;

import java.io.IOException;
import java.io.InputStream;

/**
 * Strategy interface for document extraction implementations.
 */
public interface DocumentExtractor {

    /** Returns true if this extractor can process the given content type or filename */
    boolean supports(String contentType, String fileName);

    /** Extracts clean text and metrics from the given stream */
    ExtractionResult extract(InputStream inputStream, String fileName) throws IOException;

    /** Identifier for the extraction strategy */
    String getName();
}
