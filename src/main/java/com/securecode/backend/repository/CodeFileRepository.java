package com.securecode.backend.repository;

import com.securecode.backend.entity.CodeFile;
import com.securecode.backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodeFileRepository extends JpaRepository<CodeFile, Long> {

    List<CodeFile> findByProject(Project project);
}