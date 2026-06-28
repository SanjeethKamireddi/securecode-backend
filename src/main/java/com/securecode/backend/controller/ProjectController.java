package com.securecode.backend.controller;

import com.securecode.backend.entity.Project;
import com.securecode.backend.entity.User;
import com.securecode.backend.repository.ProjectRepository;
import com.securecode.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectController(ProjectRepository projectRepository,
                             UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // CREATE a project (owned by the logged-in user)
    @PostMapping
    public Project createProject(@RequestBody Project project, Authentication authentication) {
        // 1. Who is making this request? (username came from the JWT)
        String username = authentication.getName();

        // 2. Fetch the real User from the database (gives us the id)
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Set this user as the owner, then save
        project.setOwner(currentUser);
        return projectRepository.save(project);
    }

    // LIST the logged-in user's own projects
    @GetMapping
    public List<Project> getMyProjects(Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return projectRepository.findByOwner(currentUser);
    }
}