
package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.exception.FileCouldNotBeSaved;
import com.elit.outildedecision.exception.ProjectFolderAlreadyExistsException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ProjectOwnerException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.ContributerModel;
import com.elit.outildedecision.model.ProjectModel;
import com.elit.outildedecision.model.ProjectResponseModel;
import org.apache.tomcat.util.json.ParseException;
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
   * @param userName user name
   * @return a ProjectResponseModel object representing the newly created project
   * @throws ProjectFolderAlreadyExistsException if a folder with the same name already exists.
   * @throws UserNotFoundException if the chef or a contributer user cannot be found.
   * @throws ProjectNotFoundException if the project cannot be found.
   * @throws IOException if an I/O error occurs while creating the project directory.
   * @throws ParseException if there is an error parsing the multicriteria analysis form.
   * @throws org.json.simple.parser.ParseException if there is an error parsing the multicriteria analysis form JSON.
   */
  ProjectResponseModel persistProject(ProjectModel project, String userName) throws ProjectFolderAlreadyExistsException,
    UserNotFoundException,
    ProjectNotFoundException,
    IOException,
    ParseException,
    org.json.simple.parser.ParseException, FileCouldNotBeSaved;

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
   * @throws ProjectNotFoundException if the specified project does not exist
   * @throws UserNotFoundException if the specified user does not exist
   */
  ResponseEntity<String> addProjectContributor(Long projectId, ContributerModel contributor)
    throws ProjectNotFoundException, UserNotFoundException;

  /**
   * Deletes a contributor from the specified project.
   *
   * @param projectId the ID of the project to delete the contributor from
   * @param contributorID the ID of the contributor to delete
   * @return a ResponseEntity indicating the success or failure of the delete operation
   * @throws ProjectNotFoundException if the project with the given ID does not exist
   * @throws UserNotFoundException if the user with the given ID does not exist in the project
   * @throws ProjectOwnerException if the contributor to be removed is the owner of the project
   */
  ResponseEntity<String> deleteProjectContributor(Long projectId, Long contributorID)
    throws ProjectOwnerException, UserNotFoundException, ProjectNotFoundException;

/**
 * Retrieves the data for the project with the specified ID.
 *
 * @param projectId the ID of the project to retrieve the data for
 * @return a ProjectResponseModel object representing the project data
 * @throws ProjectNotFoundException if the specified project does not exist
 * @throws UserNotAuthorizedException if the user is not authorized to access the project
 */
  ProjectResponseModel getProjectData(Long projectId) throws ProjectNotFoundException, UserNotAuthorizedException, ExecutionPlanNotFoundException;
}
