package org.prokopchuk.facultymcpserver.benchmark.dto;

import java.util.List;
import java.util.UUID;

public record TestQuestion(
        UUID id,
        String question,
        List<String> documents,
        List<String> labels,
        String comment
) {
}
