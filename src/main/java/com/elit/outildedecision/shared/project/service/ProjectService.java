package com.elit.outildedecision.shared.project.service;

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
import com.elit.outildedecision.shared.util.FormProgressStatus;
import org.apache.tomcat.util.json.ParseException;

import java.io.IOException;
import java.util.List;

/**
 * Contract for the project service: creation, retrieval, update and deletion of projects and
 * their related data. The service exposes DTOs and plain results, never HTTP envelopes.
 */
public interface ProjectService {

  /**
   * Persists a new project.
   *
   * @param project  the project to create
   * @param userName the email of the connected user
   * @return the newly created project
   * @throws UserNotFoundException                 if the chef or a contributor cannot be found
   * @throws ProjectNotFoundException              if the project cannot be found
   * @throws IOException                           if the multicriteria analysis form cannot be read
   * @throws ParseException                        if the multicriteria analysis form cannot be parsed
   * @throws org.json.simple.parser.ParseException if the multicriteria analysis form JSON is invalid
   * @throws FileCouldNotBeSaved                   if an uploaded file cannot be saved
   */
  ProjectResponse persistProject(ProjectRequest project, String userName)
      throws UserNotFoundException,
      ProjectNotFoundException,
      IOException,
      ParseException,
      org.json.simple.parser.ParseException, FileCouldNotBeSaved;

  /**
   * Retrieves all projects.
   *
   * @return the list of all projects
   */
  List<ProjectResponse> getAllProjects();

  /**
   * Deletes a project and all its related data.
   *
   * @param id the id of the project to delete
   * @return the deletion confirmation message
   * @throws ProjectNotFoundException    if the project does not exist
   * @throws IOException                 if the project folder cannot be deleted
   * @throws AttachmentNotFoundException if an attachment cannot be found
   * @throws ResponseNotFoundException   if a related response cannot be found
   */
  String deleteProject(Long id)
      throws ProjectNotFoundException, IOException, AttachmentNotFoundException, ResponseNotFoundException;

  /**
   * Retrieves the projects of the connected user matching the given keyword.
   *
   * @param keyword the keyword to search for
   * @return the matching projects
   */
  List<ProjectResponse> getUserProjects(String keyword);

  /**
   * Retrieves the paged list of projects of the connected user.
   *
   * @param triModel the filter, sort and pagination criteria
   * @return the paged projects together with the created/affected totals
   * @throws UserNotAuthorizedException     if the user is not authorized to access a project
   * @throws ExecutionPlanNotFoundException if an execution plan is not found
   * @throws ProjectNotFoundException       if a project is not found
   */
  ProjectPageResponse getUserProjectList(ProjectFilterRequest triModel)
      throws UserNotAuthorizedException, ExecutionPlanNotFoundException, ProjectNotFoundException;

  /**
   * Updates an existing project.
   *
   * @param id           the id of the project to update
   * @param projectModel the updated project data
   * @return the updated project
   * @throws ProjectNotFoundException if the project does not exist
   * @throws IOException              if an I/O error occurs while updating the project
   */
  ProjectResponse updateProject(Long id, ProjectRequest projectModel) throws ProjectNotFoundException, IOException;

  /**
   * Validates or rejects a project.
   *
   * @param id        the id of the project to validate or reject
   * @param confirmed {@code true} to validate, {@code false} to reject
   * @return the validation outcome
   * @throws ProjectNotFoundException if the project does not exist
   */
  ProjectValidationResult validateOrRejectProject(Long id, boolean confirmed) throws ProjectNotFoundException;

  /**
   * Adds a contributor to a project.
   *
   * @param projectId   the id of the project
   * @param contributor the contributor to add
   * @return the confirmation message
   * @throws ProjectNotFoundException if the project does not exist
   * @throws UserNotFoundException    if the user does not exist
   */
  String addProjectContributor(Long projectId, ContributorRequest contributor)
      throws ProjectNotFoundException, UserNotFoundException;

  /**
   * Removes a contributor from a project.
   *
   * @param projectId     the id of the project
   * @param contributorID the id of the contributor to remove
   * @return the confirmation message
   * @throws ProjectNotFoundException if the project does not exist
   * @throws UserNotFoundException    if the contributor does not exist in the project
   * @throws ProjectOwnerException    if the contributor is the owner of the project
   */
  String deleteProjectContributor(Long projectId, Long contributorID)
      throws ProjectOwnerException, UserNotFoundException, ProjectNotFoundException;

  /**
   * Retrieves the data of a project.
   *
   * @param projectId the id of the project
   * @return the project data
   * @throws ProjectNotFoundException       if the project does not exist
   * @throws UserNotAuthorizedException     if the user is not authorized to access the project
   * @throws ExecutionPlanNotFoundException if an execution plan is not found
   */
  ProjectResponse getProjectData(Long projectId)
      throws ProjectNotFoundException, UserNotAuthorizedException, ExecutionPlanNotFoundException;

  /**
   * Updates the form type status of a project.
   *
   * @param projectId         the id of the project to update
   * @param newFormTypeStatus the new form type status
   * @return {@code true} if the status was updated
   * @throws ProjectNotFoundException if the project does not exist
   */
  boolean updateFormTypeStatus(Long projectId, FormProgressStatus newFormTypeStatus) throws ProjectNotFoundException;
}
