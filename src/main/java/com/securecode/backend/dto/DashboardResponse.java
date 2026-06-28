package com.securecode.backend.dto;

public class DashboardResponse {

    private long totalProjects;
    private long totalFiles;

    public DashboardResponse(long totalProjects, long totalFiles) {
        this.totalProjects = totalProjects;
        this.totalFiles = totalFiles;
    }

    public long getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(long totalProjects) {
        this.totalProjects = totalProjects;
    }

    public long getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(long totalFiles) {
        this.totalFiles = totalFiles;
    }
}