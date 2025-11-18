package com.app.gestiondocumental.model;

import jakarta.persistence.*;
import java.time.Instant;
import com.app.gestiondocumental.model.FileStatus; // Import añadido si lo necesitas

@Entity
@Table(name = "file_record", schema = "trd", indexes = { // <-- LÍNEA CORREGIDA
        @Index(name = "idx_md5", columnList = "md5", unique = true)
})
public class FileRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1024)
    private String originPath;
    private long size;

    @Column(length = 64, unique = true)
    private String md5;

    private Instant mtime;
    private Instant ctime;

    private String mime;

    @Enumerated(EnumType.STRING)
    private FileStatus status;

    @Column(length = 1024)
    private String cause; // motivo de no_procesable

    private Long originalId; // referencia si duplicado

    private Instant processedAt;

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOriginPath() { return originPath; }
    public void setOriginPath(String originPath) { this.originPath = originPath; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }

    public String getMd5() { return md5; }
    public void setMd5(String md5) { this.md5 = md5; }

    public Instant getMtime() { return mtime; }
    public void setMtime(Instant mtime) { this.mtime = mtime; }

    public Instant getCtime() { return ctime; }
    public void setCtime(Instant ctime) { this.ctime = ctime; }

    public String getMime() { return mime; }
    public void setMime(String mime) { this.mime = mime; }

    public FileStatus getStatus() { return status; }
    public void setStatus(FileStatus status) { this.status = status; }

    public String getCause() { return cause; }
    public void setCause(String cause) { this.cause = cause; }

    public Long getOriginalId() { return originalId; }
    public void setOriginalId(Long originalId) { this.originalId = originalId; }

    public Instant getProcessedAt() { return processedAt; }
    public void setProcessedAt(Instant processedAt) { this.processedAt = processedAt; }
}