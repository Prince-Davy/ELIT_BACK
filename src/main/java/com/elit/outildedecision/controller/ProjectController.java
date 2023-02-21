
package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.model.ContributerModel;
import com.elit.outildedecision.model.ProjectModel;
import com.elit.outildedecision.model.ProjectResponseModel;
import com.elit.outildedecision.service.ProjectService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ProjectController contains all the endpoints related to projects.
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class ProjectController {
   /**
   * ProjectService.
   */
  private final ProjectService projectService;
  /**
   * Constructor for ProjectController.
   * dependency injection of ProjectService
   * @param projectService ProjectService
   */
  public ProjectController(final ProjectService projectService) {
    this.projectService = projectService;
  }
  /**
   * Create a new project.
   * @param project ProjectModel
   * @return ProjectResponseModel
   * @throws Exception Exception
   *
   */
  @PostMapping(value = "/user/projects", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
  public ProjectResponseModel createProject(@ModelAttribute final ProjectModel project) throws Exception {
    return projectService.persistProject(project);
  }
  /**
   * get the project data by id.
   * @param projectId the id of the project
   * @return ProjectResponseModel
   * @throws ProjectNotFoundException ProjectNotFoundException
   */
  @ApiOperation(value = "Get a product by id", notes = "Returns a product as per the id")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Not found - The product was not found")
      })
  @GetMapping("/user/projects/getdata/{projectId}")
  @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
  public ProjectResponseModel getProjectData(@PathVariable final Long projectId)
      throws ProjectNotFoundException {
    return projectService.getProjectData(projectId);
  }
  /** get the project data by id.
   * @param keyword the keyword to search for
   * @return ProjectResponseModel
   * @throws ProjectNotFoundException ProjectNotFoundException
   */
  @GetMapping("/user/projects")
  @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
  public List<Project> getProjects(@RequestParam(required = false) final String keyword) {
    return projectService.getUserProjects(keyword);
  }
  /**
   * get all projects for admin.
   * @return List<Project> list of projects
   */
  @GetMapping("/admin/projects")
  @PreAuthorize("hasAnyAuthority('ADMIN')")
  public List<Project> getAllProjects() {
    return projectService.getAllProjects();
  }
  /**
   * get all projects of a user.
   * @param keyword the keyword to search for
   * @return List<Project> list of projects
   */
  @GetMapping("/user/projectlist/")
  @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
  public List<ProjectResponseModel> getProjectList(@RequestParam(required = false) final String keyword) {
    return projectService.getUserProjectList(keyword);
  }
  /**
   * validate or reject a project.
   * @param id the id of the project
   * @param isConfirmed the confirmation status
   *                    true for validate
   *                    false for reject
   *                    null for pending
   *                    default is null
   * @throws ProjectNotFoundException ProjectNotFoundException if the project is not found
   * @throws java.io.IOException IOException
   * @return List<Project> list of projects
   */
  @PutMapping("/admin/projects/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<String> validateOrRejectProject(@PathVariable final Long id,
                                                        final HttpEntity<String> isConfirmed)
                                                        throws ProjectNotFoundException,
                                                        java.io.IOException {
    return projectService.validateOrRejectProject(id, isConfirmed);
  }
  /**
   * delete a project.
   * @param id the id of the project
   * @throws Exception Exception if the project is not found
   * @return ResponseEntity<String> the response
   */
  @DeleteMapping("/admin/projects/{id}")
  @PreAuthorize("hasAnyAuthority('ADMIN')")
  public ResponseEntity<String> deleteProjectAdmin(@PathVariable final Long id) throws Exception {
    return projectService.deleteProject(id);
  }
  /**
   * update a project.
   * @param id the id of the project
   * @throws Exception Exception if the project is not found
   * @return ResponseEntity<String> the response
   */
  @DeleteMapping("/user/projects/{id}")
  @PreAuthorize("hasAnyAuthority('USER', 'ADMIN') and isProjectOwner(#id)")
  public ResponseEntity<String> deleteProject(@PathVariable final Long id) throws Exception {
    return projectService.deleteProject(id);
  }
  /**
   * add a contributor to a project.
   * @param contributor the contributor to add
   * @param projectId the id of the project
   * @throws Exception Exception if the project is not found
   * @return ResponseEntity<String>
   */
  @PostMapping("/user/projects/{projectId}/add-contributor")
  @PreAuthorize("hasAnyAuthority('USER', 'ADMIN') and isProjectOwner(#projectId)")
  public ResponseEntity<String> addProjectContributor(
      @PathVariable final Long projectId, @ModelAttribute final ContributerModel contributor) throws Exception {
    return projectService.addProjectContributor(projectId, contributor);
  }
  /**
   * delete a contributor from a project.
   * @param projectId the id of the project
   * @param contributorID the id of the contributor
   * @return ResponseEntity<String>
   * @throws Exception Exception if the project is not found
   */
  @DeleteMapping("/user/projects/{projectId}/delete-contributor/{contributorID}")
  @PreAuthorize("hasAnyAuthority('USER', 'ADMIN') and isProjectOwner(#projectId)")
  public ResponseEntity<String> deleteProjectContributor(
      @PathVariable final Long projectId, @PathVariable final Long contributorID) throws Exception {
    return projectService.deleteProjectContributor(projectId, contributorID);
  }
}
