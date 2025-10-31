
package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.exception.AttachmentNotFoundException;
import com.elit.outildedecision.exception.FileCouldNotBeSaved;
import com.elit.outildedecision.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.exception.ProjectFolderAlreadyExistsException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ProjectOwnerException;
import com.elit.outildedecision.exception.ResponseNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.ContributerModel;
import com.elit.outildedecision.model.ProjectModel;
import com.elit.outildedecision.model.ProjectResponseModel;
import com.elit.outildedecision.model.ResponsePageProject;
import com.elit.outildedecision.model.TriModel;
import com.elit.outildedecision.service.ProjectService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.http.HttpEntity;
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

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * ProjectController contains all the endpoints related to projects.
 */
@RestController
@RequestMapping("/")
public class ProjectController {
    /**
     * ProjectService.
     */
    private final ProjectService projectService;

    /**
     * Constructor for ProjectController.
     * dependency injection of ProjectService
     *
     * @param projectService ProjectService
     */
    public ProjectController(final ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Create a new project.
     *
     * @param project ProjectModel
     * @return ProjectResponseModel
     * @throws ProjectFolderAlreadyExistsException   if a folder with the same name
     *                                               already exists.
     * @throws UserNotFoundException                 if the chef or a contributer
     *                                               user cannot be found.
     * @throws ProjectNotFoundException              if the project cannot be found.
     * @throws IOException                           if an I/O error occurs while
     *                                               creating the project directory.
     * @throws ParseException                        if there is an error parsing
     *                                               the multicriteria analysis
     *                                               form.
     * @throws org.json.simple.parser.ParseException if there is an error parsing
     *                                               the multicriteria analysis form
     *                                               JSON.
     *
     */
    @PostMapping(value = "/user/projects", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('USER')")
    public ProjectResponseModel createProject(@ModelAttribute final ProjectModel project)
            throws ProjectFolderAlreadyExistsException,
            UserNotFoundException,
            ProjectNotFoundException,
            IOException,
            ParseException,
            org.json.simple.parser.ParseException, FileCouldNotBeSaved {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) auth.getPrincipal();
        return projectService.persistProject(project, user.getUsername());
    }

    /**
     * get the project data by id.
     *
     * @param projectId the id of the project
     * @return ProjectResponseModel
     * @throws ProjectNotFoundException       ProjectNotFoundException if the
     *                                        project is not found
     * @throws UserNotAuthorizedException     UserNotAuthorizedException if the user
     *                                        is not authorized
     * @throws ExecutionPlanNotFoundException ExecutionPlanNotFoundException if the
     *                                        execution plan is not found
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - The product was not found")
    })
    @GetMapping("/user/projects/getdata/{projectId}")
    @PreAuthorize("hasAnyAuthority('USER') or hasAnyAuthority('ADMIN') or isContributer(#projectId) or isObserver(#projectId)")
    public ProjectResponseModel getProjectData(@PathVariable final Long projectId)
            throws ProjectNotFoundException, UserNotAuthorizedException, ExecutionPlanNotFoundException {
        return projectService.getProjectData(projectId);
    }

    /**
     * get the project data by id.
     *
     * @param keyword the keyword to search for
     * @return ProjectResponseModel
     *         ToDO Delete the endPoint is not used
     */
    @GetMapping("/user/projects")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<Project> getProjects(@RequestParam(required = false) final String keyword) {
        return projectService.getUserProjects(keyword);
    }

    /**
     * get all projects of a user.
     *
     * @param triModel Object contains data filter.
     * @throws UserNotAuthorizedException     UserNotAuthorizedException if the user
     *                                        is not authorized
     * @throws ExecutionPlanNotFoundException ExecutionPlanNotFoundException if the
     *                                        execution plan is not found
     * @throws ProjectNotFoundException       ProjectNotFoundException if the
     *                                        project
     *                                        is not found
     * @return List<Project> list of projects
     */
    @PostMapping("/user/projectlist/")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponsePageProject getProjectList(@RequestBody final TriModel triModel)
            throws UserNotAuthorizedException, ExecutionPlanNotFoundException, ProjectNotFoundException {
        return projectService.getUserProjectList(triModel);
    }

    /**
     * update a project.
     *
     * @param projectModel the project data to update
     * @param id           the id of the project to update
     * @return ResponseEntity<String>
     * @throws ProjectNotFoundException if the specified project does not exist
     * @throws IOException              if an I/O error occurs while updating the
     *                                  project data
     */
    @PutMapping(value = "/user/projects/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('USER') and isProjectOwner(#id)")
    public ProjectResponseModel updateProject(@PathVariable final Long id,
            @ModelAttribute final ProjectModel projectModel)
            throws ProjectNotFoundException, IOException {
        return projectService.updateProject(id, projectModel);
    }

    /**
     * update a project.
     *
     * @param id the id of the project
     * @throws ProjectNotFoundException ProjectNotFoundException if the project is
     *                                  not found
     * @throws IOException              IOException if an I/O error occurs while
     *                                  deleting the project data
     * @return ResponseEntity<String> the response
     */
    @DeleteMapping("/user/projects/{id}")
    @PreAuthorize("hasAnyAuthority('USER') and isProjectOwner(#id)")
    public ResponseEntity<String> deleteProject(@PathVariable final Long id)
      throws ProjectNotFoundException, IOException, AttachmentNotFoundException, ResponseNotFoundException {
        return projectService.deleteProject(id);
    }

    /**
     * add a contributor to a project.
     *
     * @param contributor the contributor to add
     * @param projectId   the id of the project
     * @throws ProjectNotFoundException if the specified project does not exist
     * @throws UserNotFoundException    if the specified user does not exist
     * @return ResponseEntity<String>
     */

    @PostMapping("/user/projects/{projectId}/add-contributor")
    @PreAuthorize("hasAnyAuthority('USER') and isProjectOwner(#projectId)")
    public ResponseEntity<String> addProjectContributor(
            @PathVariable final Long projectId, @Valid @ModelAttribute final ContributerModel contributor)
            throws ProjectNotFoundException, UserNotFoundException {
        return projectService.addProjectContributor(projectId, contributor);
    }

    /**
     * delete a contributor from a project.
     *
     * @param projectId     the id of the project
     * @param contributorID the id of the contributor
     * @return ResponseEntity<String>
     * @throws ProjectNotFoundException if the project with the given ID does not
     *                                  exist
     * @throws UserNotFoundException    if the user with the given ID does not exist
     *                                  in the project
     * @throws ProjectOwnerException    if the contributor to be removed is the
     *                                  owner of the project
     */
    @DeleteMapping("/user/projects/{projectId}/delete-contributor/{contributorID}")
    @PreAuthorize("hasAnyAuthority('USER') and isProjectOwner(#projectId)")
    public ResponseEntity<String> deleteProjectContributor(
            @PathVariable final Long projectId, @PathVariable final Long contributorID)
            throws ProjectOwnerException, UserNotFoundException, ProjectNotFoundException {
        return projectService.deleteProjectContributor(projectId, contributorID);
    }

    /**
     * validate or reject a project.
     *
     * @param id          the id of the project
     * @param isConfirmed the confirmation status
     *                    true for validate
     *                    false for reject
     *                    null for pending
     *                    default is null
     * @throws ProjectNotFoundException ProjectNotFoundException if the project is
     *                                  not found
     * @throws java.io.IOException      IOException
     * @return List<Project> list of projects
     */
    @PutMapping("/admin/projects/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> validateOrRejectProject(@PathVariable final Long id,
            final HttpEntity<String> isConfirmed)
            throws ProjectNotFoundException,
            IOException {
        return projectService.validateOrRejectProject(id, isConfirmed);
    }

    /**
     * get all projects for admin.
     *
     * @return List<Project> list of projects
     */
    @GetMapping("/admin/projects")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    /**
     * delete a project.
     *
     * @param id the id of the project
     * @throws ProjectNotFoundException ProjectNotFoundException if the project is
     *                                  not found
     * @throws IOException              IOException if an I/O error occurs while
     *                                  deleting the project data
     * @return ResponseEntity<String> the response
     */
    @DeleteMapping("/admin/projects/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> deleteProjectAdmin(@PathVariable final Long id)
      throws ProjectNotFoundException, IOException, AttachmentNotFoundException, ResponseNotFoundException {
        return projectService.deleteProject(id);
    }
}
