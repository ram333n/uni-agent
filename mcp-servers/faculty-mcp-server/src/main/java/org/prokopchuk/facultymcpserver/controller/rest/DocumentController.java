package org.prokopchuk.facultymcpserver.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.prokopchuk.facultymcpserver.common.dto.ApiResponse;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchRequest;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchResults;
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
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Document uploaded successfully", content = @Content(schema = @Schema(implementation = Long.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Document already exists", content = @Content)
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Long>> uploadDocument(@RequestParam("file") MultipartFile file) {
        log.info("Request on uploading document. File name: {}", file.getOriginalFilename());
        Long id = documentService.saveDocument(file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Document uploaded successfully", id));
    }

    @Operation(
            summary = "Upload a string to embeddings table",
            description = "Stores the file and indexes it for semantic search"
    )
    @PostMapping("/embed")
    public ResponseEntity<ApiResponse<UUID>> uploadString(String content) {
        UUID id = documentService.embed(content);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Content embedded successfully", id));
    }

    @Operation(
            summary = "Upload a string to embeddings table",
            description = "Stores the file and indexes it for semantic search"
    )
    @GetMapping("/semantic-search")
    public ResponseEntity<ApiResponse<SemanticSearchResults>> findBySemanticSearch(@Valid @ModelAttribute SemanticSearchRequest request) {
        log.info("Request on searching document by semantic search. Request: {}", request);

        SemanticSearchResults searchResults = documentService.findBySemanticSearch(request);

        return ResponseEntity.ok(ApiResponse.ok("Search completed successfully", searchResults));
    }

    @Operation(
            summary = "Delete document by id",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Document deleted successfully", content = @Content(schema = @Schema(implementation = Long.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Document not found", content = @Content)
            }
    )
    @DeleteMapping("/{document-id}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable("document-id") Long documentId) {
        log.info("Request on deleting document by id. Document id: {}", documentId);

        documentService.deleteDocument(documentId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.noContent("Document deleted successfully"));
    }

}
