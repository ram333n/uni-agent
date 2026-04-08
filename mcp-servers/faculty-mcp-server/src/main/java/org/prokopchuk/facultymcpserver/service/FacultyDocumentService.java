package org.prokopchuk.facultymcpserver.service;

import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchRequest;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchResults;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FacultyDocumentService {

    Long saveDocument(byte[] fileBytes, String fileName);

    UUID embed(String content);

    SemanticSearchResults findBySemanticSearch(SemanticSearchRequest request);

    void deleteDocument(Long documentId);

}
