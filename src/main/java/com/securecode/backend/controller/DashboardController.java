package com.securecode.backend.controller;

import com.securecode.backend.dto.DashboardResponse;
import com.securecode.backend.entity.Project;
import com.securecode.backend.entity.User;
import com.securecode.backend.repository.CodeFileRepository;
import com.securecode.backend.repository.ProjectRepository;
import com.securecode.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ProjectRepository projectRepository;
    private final CodeFileRepository codeFileRepository;
    private final UserRepository userRepository;

    public DashboardController(ProjectRepository projectRepository,
                               CodeFileRepository codeFileRepository,
                               UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.codeFileRepository = codeFileRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public DashboardResponse getDashboard(Authentication authentication) {
        // 1. Identify the logged-in user
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Get this user's projects
        List<Project> projects = projectRepository.findByOwner(currentUser);
        long totalProjects = projects.size();

        // 3. Count files across all those projects
        long totalFiles = 0;
        for (Project project : projects) {
            totalFiles += codeFileRepository.findByProject(project).size();
        }

        // 4. Package the counts into the DTO and return
        return new DashboardResponse(totalProjects, totalFiles);
    }
}