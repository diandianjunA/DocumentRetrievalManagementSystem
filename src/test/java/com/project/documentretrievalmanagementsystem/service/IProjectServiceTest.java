package com.project.documentretrievalmanagementsystem.service;

import com.project.documentretrievalmanagementsystem.dto.ProjectDto;
import com.project.documentretrievalmanagementsystem.dto.SimilarityDto;
import com.project.documentretrievalmanagementsystem.entity.Project;
import com.project.documentretrievalmanagementsystem.mapper.ProjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class IProjectServiceTest {

    @Autowired
    ProjectMapper projectMapper;
    @Value("${my.basePathT}")
    private String mybasePath;
    @Autowired
    IProjectService projectService;
    @Autowired
    ISchemeService schemeService;

    @Test
    void getProjectMap() {
        Map<Integer, Project> projectMap = projectService.getProjectMap();
        System.out.println(projectMap);
    }

    @Test
    void similarity() throws IOException {
        double similarity = projectService.similarity(3, 4);
        System.out.println(similarity);
    }

    @Test
    void projectAnalyze() throws IOException {
        List<ProjectDto> projectDtos = projectService.projectAnalyze(1);
        for (ProjectDto projectDto : projectDtos) {
            System.out.println(projectDto);
        }
    }

    @Test
    void similarityAnalyze() throws IOException {
        SimilarityDto projectDtos = projectService.similarityAnalyze(1,2);
        System.out.println(projectDtos);
    }
}