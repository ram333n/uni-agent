package org.prokopchuk.facultymcpserver.benchmark;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("semantic-search-benchmark")
@Testcontainers
@SpringBootTest
@TestPropertySource(properties = {
        "semantic-search-benchmark.test-docs.root=classpath:/semantic-search-benchmark"
})
public class SemanticSearchBenchmarkTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> POSTGRESQL_CONTAINER
            = new PostgreSQLContainer<>("pgvector/pgvector:pg16");

    @Value("${semantic-search-benchmark.test-docs.root}")
    private String testDocsRoot;

    @Test
    void print() {
        System.out.println(testDocsRoot);
    }

}
