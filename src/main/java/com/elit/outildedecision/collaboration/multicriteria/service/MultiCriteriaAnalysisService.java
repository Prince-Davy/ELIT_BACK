package com.elit.outildedecision.collaboration.multicriteria.service;

import com.elit.outildedecision.collaboration.multicriteria.dto.MultiCriteriaAnalysisScoreResponse;
import com.elit.outildedecision.collaboration.multicriteria.dto.MultiCriteriaStepRequest;
import com.elit.outildedecision.collaboration.multicriteria.dto.MulticriteriaAnalysisResponse;
import com.elit.outildedecision.collaboration.multicriteria.exception.EscoreException;
import com.elit.outildedecision.collaboration.multicriteria.exception.FormValidationException;
import com.elit.outildedecision.collaboration.multicriteria.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.collaboration.multicriteria.exception.StepFormNotFoundException;
import com.elit.outildedecision.collaboration.multicriteria.dto.FormQuestionModel;
import com.elit.outildedecision.shared.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.shared.exception.QuestionsNotFoundException;
import com.elit.outildedecision.shared.exception.UserNotAuthorizedException;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import org.apache.tomcat.util.json.ParseException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * This interface defines the contract for the Multi-Criteria Analysis Service, which provides methods to create, retrieve, update, and
 * calculate scores for multi-criteria analyses.
 */
public interface MultiCriteriaAnalysisService {

  /**
   * Creates a new multi-criteria analysis form for the specified project. When an analysis is
   * already in progress for the project the call is a no-op.
   *
   * @param projectId the ID of the project for which to create the form
   * @throws IOException if an I/O error occurs while reading the configuration file
   * @throws ParseException if a parse error occurs while reading the configuration file
   * @throws org.json.simple.parser.ParseException if a parse error occurs while reading the configuration file
   * @throws ProjectNotFoundException if the specified project does not exist
   */
  void createMulticriteriaAnalysisForm(Long projectId)
    throws IOException, ParseException, org.json.simple.parser.ParseException, ProjectNotFoundException;

  /**
   * Retrieves the multi-criteria analysis form for the specified project.
   *
   * @param projectId the ID of the project for which to retrieve the form
   * @return the multi-criteria analysis form, or an empty optional when none exists
   * @throws ProjectNotFoundException if the specified project does not exist
   */
  Optional<MulticriteriaAnalysisResponse> getMulticriteriaAnalysis(Long projectId) throws ProjectNotFoundException;

  /**
   * Updates the multi-criteria analysis form for the specified project.
   * Validates that all required fields (text answers and evaluation criteria) are properly filled.
   *
   * @param projectId the ID of the project for which to update the form
   * @param multiCriteriaAnalysis a step request representing the updated form
   * @throws StepFormNotFoundException if the step does not exist
   * @throws FormValidationException if validation fails (missing answers or criteria)
   */
  void updateMultiCriteriaAnalysis(
    Long projectId, MultiCriteriaStepRequest multiCriteriaAnalysis) throws StepFormNotFoundException, FormValidationException;

  /**
   * Updates an existing multi-criteria analysis question.
   *
   * @param projectId the projectId
   * @param formQuestion a FormQuestionModel object representing the question to update
   * @throws MulticriteriaNotFoundException if the question (or its analysis) does not belong to the project
   * @throws StepFormNotFoundException if the step associated to the question does not exist
   */
  void updateMultiCriteriaQuestion(Long projectId, FormQuestionModel formQuestion)
    throws MulticriteriaNotFoundException, StepFormNotFoundException;

  /**
   * Calculates the score for the specified multi-criteria analysis.
   *
   * @param projectId the ID of the project for which to calculate the score
   * @return the calculated score
   * @throws ProjectNotFoundException if the specified project does not exist
   * @throws QuestionsNotFoundException if any of the questions associated with the multi-criteria analysis do not exist
   * @throws EscoreException if an error occurs while calculating the score
   */
  MultiCriteriaAnalysisScoreResponse calculateMultiCriteriaAnalysisScore(Long projectId)
    throws ProjectNotFoundException, QuestionsNotFoundException, EscoreException;

  /**
   * Increases the iteration count for the specified multi-criteria analysis.
   *
   * @param analyseId the ID of the multi-criteria analysis for which to increase the iteration count
   * @throws MulticriteriaNotFoundException if the specified multi-criteria analysis does not exist
   * @throws UserNotAuthorizedException if the user is not authorized to update the specified multi-criteria analysis
   */
  void increaseMulticriteriaIteration(Long analyseId)
    throws MulticriteriaNotFoundException, UserNotAuthorizedException;

  /**
   * Increases the iteration count for the specified multi-criteria analysis.
   * @param projectId the Id of the project
   * @param analyseId the ID of the multi-criteria analysis for which to increase the iteration count
   * @throws MulticriteriaNotFoundException if the analysis does not exist or does not belong to the project
   * @throws UserNotAuthorizedException if the user is not authorized to update the specified multi-criteria analysis
   * @throws ProjectNotFoundException if the specified project does not exist
   */
  void increaseMulticriteriaIteration(Long projectId, Long analyseId)
    throws MulticriteriaNotFoundException, UserNotAuthorizedException, ProjectNotFoundException;

  /**
   * Marks the specified multi-criteria analysis as finished.
   *
   * @param projectId the id of the project
   * @param analyseId the id of the multi-criteria analysis to finalise
   * @throws MulticriteriaNotFoundException if the analysis does not exist or does not belong to the project
   */
  void markMultiCriteriaAnalysisAsFinished(Long projectId, Long analyseId)
    throws MulticriteriaNotFoundException;

  /**
   * Builds the self-contained multi-criteria analysis responses for every analysis of the given
   * project, including the embedded predictibility analysis and its execution-plan score. The
   * assembly lives in this context because it resolves the multi-criteria/predictibility entity
   * graph into collaboration DTOs.
   *
   * @param project the project whose multi-criteria analyses are mapped
   * @return the list of multi-criteria analysis responses (empty when the project has none)
   * @throws ExecutionPlanNotFoundException if a referenced execution plan cannot be found
   * @throws UserNotAuthorizedException if the user is not authorized to read an execution-plan score
   */
  List<MulticriteriaAnalysisResponse> buildProjectAnalyses(Project project)
    throws ExecutionPlanNotFoundException, UserNotAuthorizedException;
}
