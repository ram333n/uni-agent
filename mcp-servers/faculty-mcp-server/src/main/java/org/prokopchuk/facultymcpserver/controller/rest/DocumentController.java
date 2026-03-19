package org.prokopchuk.facultymcpserver.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchRequest;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchResults;
import org.prokopchuk.facultymcpserver.common.exception.DuplicateDocumentException;
import org.prokopchuk.facultymcpserver.service.FacultyDocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Log4j2
@RestController
@RequestMapping(path = "/documents")
@RequiredArgsConstructor
@Tag(name = "Documents", description = "Faculty document management")
public class DocumentController {

    private final FacultyDocumentService documentService;

    @Operation(
            summary = "Upload a faculty document",
            description = "Stores the file and indexes it for semantic search",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Document uploaded successfully", content = @Content(schema = @Schema(implementation = Long.class))),
                    @ApiResponse(responseCode = "409", description = "Document already exists", content = @Content)
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> uploadDocument(@RequestParam("file") MultipartFile file) {
        log.info("Request on uploading document. File name: {}", file.getOriginalFilename());
        try {
            Long id = documentService.saveDocument(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(id);
        } catch (DuplicateDocumentException e) {
            log.warn("Rejected duplicate document upload: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(
            summary = "Upload a string to embeddings table",
            description = "Stores the file and indexes it for semantic search"
    )
    @PostMapping("/embed")
    public ResponseEntity<UUID> uploadString(String content) {
        UUID id = documentService.embed(content);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(id);
    }

    @Operation(
            summary = "Upload a string to embeddings table",
            description = "Stores the file and indexes it for semantic search"
    )
    @GetMapping("/semantic-search")
    public ResponseEntity<SemanticSearchResults> findBySemanticSearch(@Valid @ModelAttribute SemanticSearchRequest request) {
        log.info("Request on searching document by semantic search. Request: {}", request);

        SemanticSearchResults searchResults = documentService.findBySemanticSearch(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(searchResults);
    }

}
