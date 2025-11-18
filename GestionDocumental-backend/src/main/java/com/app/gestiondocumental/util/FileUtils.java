package com.app.gestiondocumental.util; // <-- PAQUETE CORREGIDO

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

public class FileUtils {

    public static String computeMD5(Path path) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (InputStream is = Files.newInputStream(path);
             DigestInputStream dis = new DigestInputStream(is, md)) {
            byte[] buffer = new byte[8 * 1024];
            while (dis.read(buffer) != -1) {
                // digest stream actualiza el digest internamente
            }
        }
        byte[] digest = md.digest();
        return HexFormat.of().formatHex(digest);
    }

    public static Instant getMtime(Path path) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        return attrs.lastModifiedTime().toInstant();
    }

    public static Instant getCtime(Path path) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        return attrs.creationTime().toInstant();
    }

    public static String probeMime(Path path) throws IOException {
        String mime = Files.probeContentType(path);
        return mime == null ? "application/octet-stream" : mime;
    }

    public static void copyPreserveAttributes(Path source, Path target) throws IOException {
        Files.createDirectories(target.getParent());
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
    }

    public static void movePreserveAttributes(Path source, Path target) throws IOException {
        Files.createDirectories(target.getParent());
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}