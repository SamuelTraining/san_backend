package com.example.crawler.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManifestGenerator {
    private final Path manifestDir;
    private final ObjectMapper mapper = new ObjectMapper();

    private final List<Map<String, Object>> entries = new ArrayList<>();

    public ManifestGenerator(Path manifestDir) {
        this.manifestDir = manifestDir;
    }

    public void addEntry(Map<String, Object> entry) {
        entries.add(entry);
    }

    public Path write(String repoName) throws Exception {
        Files.createDirectories(manifestDir);
        // Formatear Instant usando zona por defecto para evitar UnsupportedTemporalTypeException
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now());
        String safeRepoName = repoName == null ? "repo" : repoName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
        String fileName = String.format("manifest-%s-%s.json", safeRepoName, timestamp);
        Path out = manifestDir.resolve(fileName);
        mapper.writerWithDefaultPrettyPrinter().writeValue(out.toFile(), entries);
        return out;
    }
}