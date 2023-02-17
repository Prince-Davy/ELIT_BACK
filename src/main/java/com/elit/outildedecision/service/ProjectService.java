package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.model.ContributerModel;
import com.elit.outildedecision.model.ProjectModel;
import com.elit.outildedecision.model.ProjectResponseModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface ProjectService {
  ProjectResponseModel persistProject(ProjectModel project) throws Exception;

  List<Project> getAllProjects();

  ResponseEntity<String> deleteProject(Long id) throws ProjectNotFoundException, IOException;

  Project updateProject(Long id, Project project) throws ProjectNotFoundException;

  Project getProjectById(Long projectId) throws ProjectNotFoundException;

  List<Project> getUserProjects(String keyword);

  List<ProjectResponseModel> getUserProjectList(String keyword);

  ResponseEntity<String> validateOrRejectProject(Long id, HttpEntity<String> isConfirmed)
      throws ProjectNotFoundException, IOException;

  ResponseEntity<String> addProjectContributor(Long projectId, ContributerModel contributor)
      throws Exception;

  ResponseEntity<String> deleteProjectContributor(Long projectId, Long contributorID)
      throws Exception;

  ProjectResponseModel getProjectData(Long projectId) throws ProjectNotFoundException;
}
