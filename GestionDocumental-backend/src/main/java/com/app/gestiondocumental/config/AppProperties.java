package com.app.gestiondocumental.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.crawler")
public class AppProperties {
    private String duplicatesDir;
    private String notprocessableDir;
    private String manifestDir;
    private List<String> supportedMimeTypes;

    public String getDuplicatesDir() { return duplicatesDir; }
    public void setDuplicatesDir(String duplicatesDir) { this.duplicatesDir = duplicatesDir; }

    public String getNotprocessableDir() { return notprocessableDir; }
    public void setNotprocessableDir(String notprocessableDir) { this.notprocessableDir = notprocessableDir; }

    public String getManifestDir() { return manifestDir; }
    public void setManifestDir(String manifestDir) { this.manifestDir = manifestDir; }

    public List<String> getSupportedMimeTypes() { return supportedMimeTypes; }
    public void setSupportedMimeTypes(List<String> supportedMimeTypes) { this.supportedMimeTypes = supportedMimeTypes; }
}