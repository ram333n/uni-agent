package org.prokopchuk.facultymcpserver.benchmark;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.prokopchuk.facultymcpserver.service.FacultyDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.Resource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Log4j2
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

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) throws IOException {
        Path tempStorage = Files.createTempDirectory("semantic-search-benchmark");
        registry.add("storage.documents.path", tempStorage::toString);
    }

    @Value("${semantic-search-benchmark.test-docs.root}")
    private Resource testDocsRoot;
    private Path testDocsRootDir;
    private Path testDocsDir;

    @Value("${storage.documents.path}")
    private String storageDocumentsPathStr;
    private Path storageDocumentsPathDir;

    @Autowired
    private FacultyDocumentService facultyDocumentService;

    @PostConstruct
    void init() throws IOException {
        this.testDocsRootDir = Path.of(testDocsRoot.getURI());
        this.testDocsDir = testDocsRootDir.resolve("docs");
        this.storageDocumentsPathDir = Path.of(storageDocumentsPathStr);

        FileUtils.cleanDirectory(storageDocumentsPathDir.toFile());
    }

    @PreDestroy
    void clean() throws IOException {
        FileUtils.deleteDirectory(this.storageDocumentsPathDir.toFile());
    }

    @Test
    void launchSemanticSearchBenchmark() throws IOException {
        saveDocuments();
    }

    private void saveDocuments() throws IOException {
        log.info("Starting saving documents...");

        long totalStart = System.nanoTime();

        try (Stream<Path> stream = Files.list(testDocsDir)) {
            stream.filter(Files::isRegularFile)
                    .forEach(this::saveDocument);
        }

        long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - totalStart);

        log.info("Finish saving documents. Elapsed {} ms", elapsed);
    }

    private void saveDocument(Path file) {
        try {
            byte[] bytes = Files.readAllBytes(file);
            String fileName = file.getFileName().toString();

            long start = System.nanoTime();
            Long docId = facultyDocumentService.saveDocument(bytes, fileName);
            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

            log.info("Saved: {} -> id={} ({} ms)", fileName, docId, elapsed);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to save: " + file.getFileName(), e);
        }
    }

}
