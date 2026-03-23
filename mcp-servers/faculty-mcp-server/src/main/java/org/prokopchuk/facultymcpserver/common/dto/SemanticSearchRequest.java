package org.prokopchuk.facultymcpserver.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import jakarta.validation.constraints.*;

public record SemanticSearchRequest(
        @Min(1)
        @Max(50)
        @JsonPropertyDescription("Number of top results to return (1–50)")
        @JsonProperty(required = true, defaultValue = "5")
        int topK,

        @NotBlank
        @JsonPropertyDescription("Natural language query to search documents semantically")
        @JsonProperty(required = true)
        String query,

        @DecimalMin(value = "0.05")
        @DecimalMax(value = "1.0")
        @JsonPropertyDescription("Minimum cosine similarity score (0.05–1.0) to include a result")
        Double similarityThreshold
) {
}
