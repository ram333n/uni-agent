package org.prokopchuk.facultymcpserver.service.impl;

import lombok.extern.log4j.Log4j2;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchRequest;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchResultEntry;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchResults;
import org.prokopchuk.facultymcpserver.common.exception.DuplicateDocumentException;
import org.prokopchuk.facultymcpserver.domain.FacultyDocument;
import org.prokopchuk.facultymcpserver.exception.ResourceNotFoundException;
import org.prokopchuk.facultymcpserver.repository.FacultyDocumentRepository;
import org.prokopchuk.facultymcpserver.service.FacultyDocumentService;
import org.prokopchuk.facultymcpserver.service.FileService;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

@Log4j2
@Service
public class FacultyDocumentServiceImpl implements FacultyDocumentService {

    private static final double DEFAULT_SIMILARITY_THRESHOLD = 0.5;
    private static final String DOCUMENT_ID_KEY = "documentId";
    private final FacultyDocumentRepository documentRepository;
    private final FileService fileService;
    private final VectorStore vectorStore;
    private final TokenTextSplitter tokenTextSplitter;

    public FacultyDocumentServiceImpl(
            FacultyDocumentRepository documentRepository,
            FileService fileService,
            @Qualifier("facultyDocumentVectorStore") VectorStore vectorStore,
            TokenTextSplitter tokenTextSplitter
    ) {
        this.documentRepository = documentRepository;
        this.fileService = fileService;
        this.vectorStore = vectorStore;
        this.tokenTextSplitter = tokenTextSplitter;
    }

    @Override
    @Transactional
    public Long saveDocument(byte[] fileBytes, String fileName) {
        String contentHash = fileService.computeSha256Hex(fileBytes);

        log.debug("Uploading document '{}', SHA-256: {}", fileName, contentHash);

        if (documentRepository.existsByContentHash(contentHash)) {
            log.warn("Duplicate document detected, hash: {}", contentHash);
            throw new DuplicateDocumentException(contentHash);
        }

        Long documentId = saveDocument(fileName, fileBytes, contentHash);
        saveDocumentEmbeddings(documentId, fileBytes);

        return documentId;
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

    private void saveDocumentEmbeddings(Long documentId, byte[] fileBytes) {
        Resource resource = new ByteArrayResource(fileBytes);
        TikaDocumentReader docReader = new TikaDocumentReader(resource);

        List<Document> rawDocuments = docReader.read();
        List<Document> chunks = tokenTextSplitter.split(rawDocuments);

        for (int i = 0; i < chunks.size(); i++) {
            chunks.get(i).getMetadata().put(DOCUMENT_ID_KEY, documentId.toString());
            chunks.get(i).getMetadata().put("chunkId", Integer.toString(i));
        }

        vectorStore.add(chunks);
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
                Long.valueOf(document.getMetadata().get(DOCUMENT_ID_KEY).toString())
        );
    }

    @Override
    @Transactional
    public void deleteDocument(Long documentId) {
        Optional<FacultyDocument> documentOpt = documentRepository.findById(documentId);

        if (documentOpt.isEmpty()) {
            throw new ResourceNotFoundException("Document with id " + documentId + " does not exist");
        }

        deleteEmbeddingsByDocumentId(documentId);
        deleteDocumentFile(documentOpt.get());

    }

    private void deleteEmbeddingsByDocumentId(Long documentId) {
        vectorStore.delete(String.format("%s == '%s'", DOCUMENT_ID_KEY, documentId));
    }

    private void deleteDocumentFile(FacultyDocument document) {
        fileService.deleteFile(document.getFilePath());
        documentRepository.deleteById(document.getId());
    }

}
