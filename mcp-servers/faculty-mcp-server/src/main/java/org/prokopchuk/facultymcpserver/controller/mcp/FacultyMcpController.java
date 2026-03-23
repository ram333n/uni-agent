package org.prokopchuk.facultymcpserver.controller.mcp;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchRequest;
import org.prokopchuk.facultymcpserver.common.dto.SemanticSearchResults;
import org.prokopchuk.facultymcpserver.service.FacultyDocumentService;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
@RequiredArgsConstructor
public class FacultyMcpController {

    private final FacultyDocumentService facultyDocumentService;

    @McpTool(
            name = "semantic_search_documents",
            description = "Search faculty documents by semantic similarity to the given query.",
            generateOutputSchema = true,
            annotations = @McpTool.McpAnnotations(
                    readOnlyHint = true,
                    destructiveHint = false,
                    idempotentHint = true
            )
    )
    public SemanticSearchResults semanticSearchDocuments(
            @McpToolParam(description = "Search request object") SemanticSearchRequest request
    ) {
        log.info("Invoking 'semanticSearchDocuments' tool. Request: {}", request);

        return facultyDocumentService.findBySemanticSearch(request);
    }

}
