package org.prokopchuk.facultymcpserver.service.impl;

import lombok.extern.log4j.Log4j2;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchRequest;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchResultEntry;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchResults;
import org.prokopchuk.facultymcpserver.common.exception.DuplicateDocumentException;
import org.prokopchuk.facultymcpserver.domain.FacultyDocument;
import org.prokopchuk.facultymcpserver.repository.FacultyDocumentRepository;
import org.prokopchuk.facultymcpserver.service.FacultyDocumentService;
import org.prokopchuk.facultymcpserver.service.FileService;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Log4j2
@Service
public class FacultyDocumentServiceImpl implements FacultyDocumentService {

    private static final double DEFAULT_SIMILARITY_THRESHOLD = 0.5;
    private final FacultyDocumentRepository documentRepository;
    private final FileService fileService;
    private final VectorStore vectorStore;

    public FacultyDocumentServiceImpl(
            FacultyDocumentRepository documentRepository,
            FileService fileService,
            @Qualifier("facultyDocumentVectorStore") VectorStore vectorStore
    ) {
        this.documentRepository = documentRepository;
        this.fileService = fileService;
        this.vectorStore = vectorStore;
    }

    @Override
    @Transactional
    public Long saveDocument(MultipartFile file) {
        byte[] fileBytes = readFileBytes(file);
        String contentHash = fileService.computeSha256Hex(fileBytes);

        log.debug("Uploading document '{}', SHA-256: {}", file.getOriginalFilename(), contentHash);

        if (documentRepository.existsByContentHash(contentHash)) {
            log.warn("Duplicate document detected, hash: {}", contentHash);
            throw new DuplicateDocumentException(contentHash);
        }

        return saveDocument(file.getOriginalFilename(), fileBytes, contentHash);
    }

    private byte[] readFileBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read uploaded file bytes", e);
        }
    }

    private Long saveDocument(String fileName, byte[] fileBytes, String contentHash) {
        Path storedPath = fileService.persistToDisk(fileName, fileBytes);

        FacultyDocument document = FacultyDocument.builder()
                .fileName(fileName)
                .filePath(storedPath.toString())
                .contentHash(contentHash)
                .uploadedAt(LocalDateTime.now())
                .build();

        FacultyDocument saved = documentRepository.save(document);

        return saved.getId();
    }

    @Override
    public UUID embed(String content) {
        UUID documentId = UUID.randomUUID();
        Document document = new Document(
                documentId.toString(),
                content,
                Map.of(
                        "status", "test"
                )
        );

        vectorStore.add(List.of(document));

        return documentId;
    }

    @Override
    public SemanticSearchResults findBySemanticSearch(SemanticSearchRequest request) {
        SearchRequest vectorStoreRequest = SearchRequest.builder()
                .topK(request.topK())
                .query(request.query())
                .similarityThreshold(Objects.requireNonNullElse(request.similarityThreshold(), DEFAULT_SIMILARITY_THRESHOLD))
                .build();

        List<SemanticSearchResultEntry> results = vectorStore.similaritySearch(vectorStoreRequest).stream()
                .map(this::mapToSemanticSearchResultEntry)
                .toList();

        return new SemanticSearchResults(results);
    }

    private SemanticSearchResultEntry mapToSemanticSearchResultEntry(Document document) {
        return new SemanticSearchResultEntry(
                document.getText(),
                document.getScore(),
                null //TODO: link to original document
        );
    }

}
