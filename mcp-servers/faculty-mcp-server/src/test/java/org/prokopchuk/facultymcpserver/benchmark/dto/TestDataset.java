package org.prokopchuk.facultymcpserver.benchmark.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

public record TestDataset (
        String name,

        @JsonProperty("created_at")
        OffsetDateTime createdAt,

        List<TestQuestion> questions
) {

}
