
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
/**
 * This interface defines the contract for the Project Service, which provides methods to create, retrieve,
 * update, and delete projects and associated data.
 */
public interface ProjectService {

  /**
   * Persists a new project.
   *
   * @param project a ProjectModel object representing the project to create
   * @return a ProjectResponseModel object representing the newly created project
   * @throws Exception if an error occurs while persisting the project
   */
  ProjectResponseModel persistProject(ProjectModel project) throws Exception;

  /**
   * Retrieves a list of all projects.
   *
   * @return a List of Project objects representing all projects
   */
  List<Project> getAllProjects();

  /**
   * Deletes a project with the specified ID.
   *
   * @param id the ID of the project to delete
   * @return a ResponseEntity indicating the success or failure of the delete operation
   * @throws ProjectNotFoundException if the specified project does not exist
   * @throws IOException if an I/O error occurs while deleting the project data
   */
  ResponseEntity<String> deleteProject(Long id) throws ProjectNotFoundException, IOException;

  /**
   * Retrieves a list of projects for the specified user that match the specified keyword.
   *
   * @param keyword a String representing the keyword to search for
   * @return a List of Project objects representing the matching projects
   */
  List<Project> getUserProjects(String keyword);

  /**
   * Retrieves a list of project response models for the specified user that match the specified keyword.
   *
   * @param keyword a String representing the keyword to search for
   * @return a List of ProjectResponseModel objects representing the matching projects
   */
  List<ProjectResponseModel> getUserProjectList(String keyword);

  /**
   * Validates or rejects a project with the specified ID.
   *
   * @param id the ID of the project to validate or reject
   * @param isConfirmed an HttpEntity containing a String representing whether or not to confirm the project
   * @return a ResponseEntity indicating the success or failure of the validation or rejection operation
   * @throws ProjectNotFoundException if the specified project does not exist
   * @throws IOException if an I/O error occurs while updating the project data
   */
  ResponseEntity<String> validateOrRejectProject(Long id, HttpEntity<String> isConfirmed)
    throws ProjectNotFoundException, IOException;

  /**
   * Adds a contributor to the specified project.
   *
   * @param projectId the ID of the project to add the contributor to
   * @param contributor a ContributerModel object representing the contributor to add
   * @return a ResponseEntity indicating the success or failure of the add operation
   * @throws Exception if an error occurs while adding the contributor
   */
  ResponseEntity<String> addProjectContributor(Long projectId, ContributerModel contributor)
    throws Exception;

  /**
   * Deletes a contributor from the specified project.
   *
   * @param projectId the ID of the project to delete the contributor from
   * @param contributorID the ID of the contributor to delete
   * @return a ResponseEntity indicating the success or failure of the delete operation
   * @throws Exception if an error occurs while deleting the contributor
   */
  ResponseEntity<String> deleteProjectContributor(Long projectId, Long contributorID)
    throws Exception;

/**
 * Retrieves the data for the project with the specified ID.
 *
 * @param projectId the ID of the project to retrieve the data for
 * @return a ProjectResponseModel object representing the project data
 * @throws ProjectNotFoundException if the specified project does not exist
 */
  ProjectResponseModel getProjectData(Long projectId) throws ProjectNotFoundException;
}
