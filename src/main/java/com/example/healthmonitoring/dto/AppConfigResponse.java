package com.example.healthmonitoring.dto;

import com.example.healthmonitoring.enums.Platform;

import java.time.LocalDateTime;

public class AppConfigResponse {

    private String appCode;
    private String appName;
    private String modelName;
    private Platform platform;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AppConfigResponse(String appCode, String appName, String modelName, Platform platform, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.appCode = appCode;
        this.appName = appName;
        this.modelName = modelName;
        this.platform = platform;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}