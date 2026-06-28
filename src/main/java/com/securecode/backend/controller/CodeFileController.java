package com.securecode.backend.controller;

import com.securecode.backend.entity.CodeFile;
import com.securecode.backend.entity.Project;
import com.securecode.backend.entity.User;
import com.securecode.backend.repository.CodeFileRepository;
import com.securecode.backend.repository.ProjectRepository;
import com.securecode.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/files")
public class CodeFileController {

    private final CodeFileRepository codeFileRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public CodeFileController(CodeFileRepository codeFileRepository,
                              ProjectRepository projectRepository,
                              UserRepository userRepository) {
        this.codeFileRepository = codeFileRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // UPLOAD a file into a project
    @PostMapping
    public CodeFile uploadFile(@PathVariable Long projectId,
                               @RequestParam("file") MultipartFile file,
                               Authentication authentication) throws IOException {

        // 1. Find the project, and make sure it belongs to the logged-in user
        Project project = getOwnedProject(projectId, authentication);

        // 2. Build the CodeFile from the uploaded file
        CodeFile codeFile = new CodeFile();
        codeFile.setFileName(file.getOriginalFilename());
        codeFile.setContent(new String(file.getBytes()));
        codeFile.setProject(project);

        // 3. Save it
        return codeFileRepository.save(codeFile);
    }

    // LIST files in a project
    @GetMapping
    public List<CodeFile> listFiles(@PathVariable Long projectId,
                                    Authentication authentication) {
        Project project = getOwnedProject(projectId, authentication);
        return codeFileRepository.findByProject(project);
    }

    // Helper: fetch the project and verify the current user owns it
    private Project getOwnedProject(Long projectId, Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You do not own this project");
        }

        return project;
    }
}