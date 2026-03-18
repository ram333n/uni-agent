package org.prokopchuk.facultymcpserver.service.impl;

import lombok.RequiredArgsConstructor;
import org.prokopchuk.facultymcpserver.config.properties.StorageProperties;
import org.prokopchuk.facultymcpserver.service.FileService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final StorageProperties storageProperties;

    @Override
    public String computeSha256Hex(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    @Override
    public Path persistToDisk(String fileName, byte[] fileBytes) {
        try {
            Path storageDir = Paths.get(storageProperties.getPath());
            Files.createDirectories(storageDir);
            Path targetPath = storageDir.resolve(fileName);
            Files.write(targetPath, fileBytes);
            return targetPath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file to storage: " + storageProperties.getPath(), e);
        }
    }

}
