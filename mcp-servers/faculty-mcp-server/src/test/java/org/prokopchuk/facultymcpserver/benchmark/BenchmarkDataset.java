package org.prokopchuk.facultymcpserver.benchmark;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class BenchmarkDataset {

    private static final String DATASET_PATH = "/benchmark/dataset.json";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static List<BenchmarkQuery> load() {
        try (InputStream is = BenchmarkDataset.class.getResourceAsStream(DATASET_PATH)) {
            if (is == null) {
                throw new IllegalStateException("Benchmark dataset not found at: " + DATASET_PATH);
            }
            return OBJECT_MAPPER.readValue(is, new TypeReference<>() {});
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load benchmark dataset", e);
        }
    }
}
