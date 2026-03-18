package org.prokopchuk.facultymcpserver.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.prokopchuk.facultymcpserver.common.exception.DuplicateDocumentException;
import org.prokopchuk.facultymcpserver.domain.FacultyDocument;
import org.prokopchuk.facultymcpserver.repository.FacultyDocumentRepository;
import org.prokopchuk.facultymcpserver.service.FacultyDocumentService;
import org.prokopchuk.facultymcpserver.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Log4j2
@Service
@RequiredArgsConstructor
public class FacultyDocumentServiceImpl implements FacultyDocumentService {

    private final FacultyDocumentRepository documentRepository;
    private final FileService fileService;

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

}
