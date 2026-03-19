package org.prokopchuk.facultymcpserver.common.dto;

import jakarta.validation.constraints.*;

public record SemanticSearchRequest(
        @Min(1)
        @Max(50)
        int topK,

        @NotBlank
        String query,

        @DecimalMin(value = "0.05")
        @DecimalMax(value = "1.0")
        Double similarityThreshold
) {
}
