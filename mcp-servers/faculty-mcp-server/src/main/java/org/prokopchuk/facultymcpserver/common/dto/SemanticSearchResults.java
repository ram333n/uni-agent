package org.prokopchuk.facultymcpserver.common.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record SemanticSearchResults(
    @JsonPropertyDescription(value = "List of matched document passages ranked by similarity")
    List<SemanticSearchResultEntry> results
) {

}
