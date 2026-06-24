package com.elit.outildedecision.shared.project.controller;

import com.elit.outildedecision.shared.attachment.exception.AttachmentNotFoundException;
import com.elit.outildedecision.shared.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.shared.exception.FileCouldNotBeSaved;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectOwnerException;
import com.elit.outildedecision.shared.exception.ResponseNotFoundException;
import com.elit.outildedecision.shared.exception.UserNotAuthorizedException;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import com.elit.outildedecision.shared.project.dto.ContributorRequest;
import com.elit.outildedecision.shared.project.dto.ProjectFilterRequest;
import com.elit.outildedecision.shared.project.dto.ProjectPageResponse;
import com.elit.outildedecision.shared.project.dto.ProjectRequest;
import com.elit.outildedecision.shared.project.dto.ProjectResponse;
import com.elit.outildedecision.shared.project.service.ProjectService;
import com.elit.outildedecision.shared.project.service.ProjectValidationResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Endpoints related to projects, covering both the user journeys ({@code /user/projects},
 * {@code /user/projectlist/}) and the admin journeys ({@code /admin/projects}).
 */
@RestController
@RequestMapping("/")
@Tag(name = "Shared - Projects", description = "Gestion des projets (parcours utilisateur et admin)")
public class ProjectController {
  /**
   * The project service.
   */
  private final ProjectService projectService;

  /**
   * Instantiates a new Project controller.
   *
   * @param projectService the project service
   */
  public ProjectController(final ProjectService projectService) {
    this.projectService = projectService;
  }

  /**
   * Creates a new project.
   *
   * @param project the project to create
   * @return the created project
   * @throws UserNotFoundException                 if the chef or a contributor cannot be found
   * @throws ProjectNotFoundException              if the project cannot be found
   * @throws IOException                           if the multicriteria analysis form cannot be read
   * @throws ParseException                        if the multicriteria analysis form cannot be parsed
   * @throws org.json.simple.parser.ParseException if the multicriteria analysis form JSON is invalid
   * @throws FileCouldNotBeSaved                   if an uploaded file cannot be saved
   */
  @PostMapping(value = "/user/projects", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyAuthority('USER')")
  public ProjectResponse createProject(@ModelAttribute final ProjectRequest project)
      throws UserNotFoundException,
      ProjectNotFoundException,
      IOException,
      ParseException,
      org.json.simple.parser.ParseException, FileCouldNotBeSaved {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserDetails user = (UserDetails) auth.getPrincipal();
    return projectService.persistProject(project, user.getUsername());
  }

  /**
   * Gets the project data by id.
   *
   * @param projectId the id of the project
   * @return the project data
   * @throws ProjectNotFoundException       if the project is not found
   * @throws UserNotAuthorizedException     if the user is not authorized
   * @throws ExecutionPlanNotFoundException if the execution plan is not found
   */
  @GetMapping("/user/projects/getdata/{projectId}")
  @PreAuthorize("hasAnyAuthority('USER') or hasAnyAuthority('ADMIN') or isContributer(#projectId) or isObserver(#projectId)")
  public ProjectResponse getProjectData(@PathVariable final Long projectId)
      throws ProjectNotFoundException, UserNotAuthorizedException, ExecutionPlanNotFoundException {
    return projectService.getProjectData(projectId);
  }

  /**
   * Gets the projects of the connected user matching the keyword.
   *
   * @param keyword the keyword to search for
   * @return the list of projects
   */
  @GetMapping("/user/projects")
  @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
  public List<ProjectResponse> getProjects(@RequestParam(required = false) final String keyword) {
    return projectService.getUserProjects(keyword);
  }

  /**
   * Gets the paged list of projects of the connected user.
   *
   * @param triModel the filter, sort and pagination criteria
   * @return the paged projects
   * @throws UserNotAuthorizedException     if the user is not authorized
   * @throws ExecutionPlanNotFoundException if the execution plan is not found
   * @throws ProjectNotFoundException       if the project is not found
   */
  @PostMapping("/user/projectlist/")
  @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
  public ProjectPageResponse getProjectList(@RequestBody final ProjectFilterRequest triModel)
      throws UserNotAuthorizedException, ExecutionPlanNotFoundException, ProjectNotFoundException {
    return projectService.getUserProjectList(triModel);
  }

  /**
   * Updates a project.
   *
   * @param id           the id of the project to update
   * @param projectModel the project data to update
   * @return the updated project
   * @throws ProjectNotFoundException if the project does not exist
   * @throws IOException              if an I/O error occurs while updating the project
   */
  @PutMapping(value = "/user/projects/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyAuthority('USER') and isProjectOwner(#id)")
  public ProjectResponse updateProject(@PathVariable final Long id,
                                       @ModelAttribute final ProjectRequest projectModel)
      throws ProjectNotFoundException, IOException {
    return projectService.updateProject(id, projectModel);
  }

  /**
   * Deletes a project.
   *
   * @param id the id of the project
   * @return the deletion confirmation message
   * @throws ProjectNotFoundException    if the project is not found
   * @throws IOException                 if an I/O error occurs while deleting the project
   * @throws AttachmentNotFoundException if an attachment cannot be found
   * @throws ResponseNotFoundException   if a related response cannot be found
   */
  @DeleteMapping("/user/projects/{id}")
  @PreAuthorize("hasAnyAuthority('USER') and isProjectOwner(#id)")
  public ResponseEntity<String> deleteProject(@PathVariable final Long id)
      throws ProjectNotFoundException, IOException, AttachmentNotFoundException, ResponseNotFoundException {
    return ResponseEntity.ok(projectService.deleteProject(id));
  }

  /**
   * Adds a contributor to a project.
   *
   * @param projectId   the id of the project
   * @param contributor the contributor to add
   * @return the confirmation message
   * @throws ProjectNotFoundException if the project does not exist
   * @throws UserNotFoundException    if the user does not exist
   */
  @PostMapping("/user/projects/{projectId}/add-contributor")
  @PreAuthorize("hasAnyAuthority('USER') and isProjectOwner(#projectId)")
  public ResponseEntity<String> addProjectContributor(
      @PathVariable final Long projectId, @Valid @ModelAttribute final ContributorRequest contributor)
      throws ProjectNotFoundException, UserNotFoundException {
    return ResponseEntity.ok(projectService.addProjectContributor(projectId, contributor));
  }

  /**
   * Removes a contributor from a project.
   *
   * @param projectId     the id of the project
   * @param contributorID the id of the contributor
   * @return the confirmation message
   * @throws ProjectNotFoundException if the project does not exist
   * @throws UserNotFoundException    if the user does not exist in the project
   * @throws ProjectOwnerException    if the contributor is the owner of the project
   */
  @DeleteMapping("/user/projects/{projectId}/delete-contributor/{contributorID}")
  @PreAuthorize("hasAnyAuthority('USER') and isProjectOwner(#projectId)")
  public ResponseEntity<String> deleteProjectContributor(
      @PathVariable final Long projectId, @PathVariable final Long contributorID)
      throws ProjectOwnerException, UserNotFoundException, ProjectNotFoundException {
    return ResponseEntity.ok(projectService.deleteProjectContributor(projectId, contributorID));
  }

  /**
   * Validates or rejects a project.
   *
   * @param id          the id of the project
   * @param isConfirmed the request body holding the {@code isConfirmed} flag
   * @return the validation result message
   * @throws ProjectNotFoundException if the project is not found
   * @throws IOException              if the request body cannot be parsed
   */
  @PutMapping("/admin/projects/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<String> validateOrRejectProject(@PathVariable final Long id,
                                                         final HttpEntity<String> isConfirmed)
      throws ProjectNotFoundException, IOException {
    Map<String, Boolean> body = new ObjectMapper().readValue(isConfirmed.getBody(),
        new TypeReference<Map<String, Boolean>>() { });
    boolean confirmed = Boolean.TRUE.equals(body.get("isConfirmed"));
    ProjectValidationResult result = projectService.validateOrRejectProject(id, confirmed);
    return switch (result) {
      case ALREADY_CONFIRMED -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ce projet est déjà validé");
      case VALIDATED -> ResponseEntity.ok().body("Le Projet a été validé");
      case ALREADY_REJECTED -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ce projet est déjà rejected");
      case REJECTED -> ResponseEntity.ok().body("Le Projet a été refusé");
    };
  }

  /**
   * Gets all projects (admin).
   *
   * @return the list of all projects
   */
  @GetMapping("/admin/projects")
  @PreAuthorize("hasAnyAuthority('ADMIN')")
  public List<ProjectResponse> getAllProjects() {
    return projectService.getAllProjects();
  }

  /**
   * Deletes a project (admin).
   *
   * @param id the id of the project
   * @return the deletion confirmation message
   * @throws ProjectNotFoundException    if the project is not found
   * @throws IOException                 if an I/O error occurs while deleting the project
   * @throws AttachmentNotFoundException if an attachment cannot be found
   * @throws ResponseNotFoundException   if a related response cannot be found
   */
  @DeleteMapping("/admin/projects/{id}")
  @PreAuthorize("hasAnyAuthority('ADMIN')")
  public ResponseEntity<String> deleteProjectAdmin(@PathVariable final Long id)
      throws ProjectNotFoundException, IOException, AttachmentNotFoundException, ResponseNotFoundException {
    return ResponseEntity.ok(projectService.deleteProject(id));
  }
}
