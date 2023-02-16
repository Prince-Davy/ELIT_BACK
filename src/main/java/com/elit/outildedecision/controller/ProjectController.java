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

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping(value = "/user/projects", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ProjectResponseModel createProject(@ModelAttribute ProjectModel project) throws Exception {
        return projectService.persistProject(project);
    }

    @ApiOperation(value = "Get a product by id", notes = "Returns a product as per the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - The product was not found")
    })
    @GetMapping("/user/projects/getdata/{projectId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ProjectResponseModel getProjectData(@PathVariable Long projectId) throws ProjectNotFoundException {
        return projectService.getProjectData(projectId);
    }

    @GetMapping("/user/projects")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<Project> getProjects(@RequestParam(required = false) String keyword) {
        return projectService.getUserProjects(keyword);
    }

    @GetMapping("/admin/projects")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<Project> getAllProjects(@RequestParam(required = false) String keyword) {
        return projectService.getAllProjects();
    }

    @GetMapping("/user/projectlist/")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<ProjectResponseModel> getProjectList(@RequestParam(required = false) String keyword) {
        return projectService.getUserProjectList(keyword);
    }

    @PutMapping("/admin/projects/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> validateOrRejectProject(@PathVariable Long id, HttpEntity<String> isConfirmed) throws Exception {
        return projectService.validateOrRejectProject(id, isConfirmed);
    }

    @DeleteMapping("/admin/projects/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity deleteProjectAdmin(@PathVariable Long id) throws Exception {
        return projectService.deleteProject(id);
    }

    @DeleteMapping("/user/projects/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN') and isProjectOwner(#id)")
    public ResponseEntity deleteProject(@PathVariable Long id) throws Exception {
        return projectService.deleteProject(id);
    }

    @PostMapping("/user/projects/{projectId}/add-contributor")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN') and isProjectOwner(#projectId)")
    public ResponseEntity<String> addProjectContributor(@PathVariable Long projectId, @ModelAttribute ContributerModel contributor) throws Exception {
        return projectService.addProjectContributor(projectId, contributor);
    }

    @DeleteMapping("/user/projects/{projectId}/delete-contributor/{contributorID}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN') and isProjectOwner(#projectId)")
    public ResponseEntity<String> deleteProjectContributor(@PathVariable Long projectId, @PathVariable Long contributorID) throws Exception {
        return projectService.deleteProjectContributor(projectId, contributorID);
    }

//
//    @DeleteMapping("/{id}")
//    public ResponseEntity deleteProject(@PathVariable Long id) throws ProjectNotFoundException, IOException {
//        return projectService.deleteProject(id);
//    }
//
//    @PutMapping("/{id}")
//    public Project updateProject(@PathVariable Long id,
//                                 @Valid @RequestBody Project project) throws ProjectNotFoundException {
//        return projectService.updateProject(id, project);
//    }
//
//
//    @GetMapping("/{id}")
//    public Project getProjectById(@PathVariable("id") Long projectId) throws ProjectNotFoundException{
//        return projectService.getProjectById(projectId);
//    }

}
