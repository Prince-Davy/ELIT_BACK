package com.elit.outildedecision.shared.util;

import com.elit.outildedecision.collaboration.brainstorming.entity.Brainstorming;
import com.elit.outildedecision.collaboration.survey.dto.SurveyAnswerRequest;
import com.elit.outildedecision.collaboration.survey.dto.SurveyAnswerResponse;
import com.elit.outildedecision.collaboration.survey.dto.SurveyQuestionRequest;
import com.elit.outildedecision.collaboration.survey.entity.SurveyQuestion;
import com.elit.outildedecision.collaboration.vote.dto.VoteRequest;
import com.elit.outildedecision.collaboration.vote.dto.VoteResponse;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.auth.entity.VerificationToken;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.collaboration.multicriteria.entity.FormStep;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.collaboration.report.dto.ReportResponse;
import com.elit.outildedecision.collaboration.report.entity.Report;
import com.elit.outildedecision.collaboration.multicriteria.dto.MultiCriteriaStepRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.elit.outildedecision.shared.auth.dto.PasswordChangeRequest;
import com.elit.outildedecision.shared.auth.dto.UserRequest;
import com.elit.outildedecision.shared.user.dto.UserResponse;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelSamples {
  public static String toJson(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static class UserSamples {
    public static Long notExistUserId = 14L;

    public static UserResponse getUserResponse() {
      return new UserResponse(1L, "username", "lastname",
        "firstname", "user@user.com",
        null);
    }

    public static UserRequest createUserRequest(String username, String userLastName,
                                            String userFirstName, String userEmail,
                                            String userPassword) {
      UserRequest userModel = new UserRequest();
      userModel.setUserFirstName(userFirstName);
      userModel.setUserLastName(userLastName);
      userModel.setUsername(username);
      userModel.setUserPassword(userPassword);
      userModel.setUserEmail(userEmail);
      userModel.setUserEmailConfirmation(userEmail);
      userModel.setUserPasswordConfirmation(userPassword);

      return userModel;
    }

    public static User getUser() {
      return createUser(1L, "username", "userLastName", "userFirstName",
        "user@user.com", "useravatarPath", "userPassword", true, true, true,
        new ArrayList<VerificationToken>(), new ArrayList<Brainstorming>());
    }
    public static User getUser2() {
      return createUser(1L, "user", "Doe", "John",
        "user2@user.com", null, "user", true, true, false,
        null, null);
    }

    public static UserRequest getUserRequest() {
      return createUserRequest("username", "lastname",
        "firstname", "user@user.com", "developer");
    }


    public static UserResponse createResponseModel(Long userId, String username,
                                                        String userLastName, String userFirstName, String userEmail,
                                                        byte[] userAvatar) {
      return new UserResponse(userId, username, userLastName,
        userFirstName, userEmail, userAvatar);

    }

    public static User createUser(Long userId, String username, String userLastName,
                                  String userFirstName, String userEmail,
                                  String userAvatarPath, String userPassword, Boolean isEmailVerified,
                                  Boolean isUserActive, Boolean isAdmin, List<VerificationToken> verificationTokens,
                                  List<Brainstorming> brainstormings) {
      {
        User user = new User();
        user.setUserId(userId);
        user.setUserFirstName(userFirstName);
        user.setUserLastName(userLastName);
        user.setUsername(username);
        user.setUserPassword(userPassword);
        user.setUserEmail(userEmail);
        user.setUserAvatarPath(userAvatarPath);
        user.setIsUserActive(isUserActive);
        user.setIsAdmin(isAdmin);
        user.setIsEmailVerified(isEmailVerified);
        user.setVerificationTokens(verificationTokens);
        user.setBrainstormings(brainstormings);
        return user;
      }

    }
  }


  public static class VoteSamples {
    public static VoteRequest getVoteModelRequest() {
      return new VoteRequest(1L, "chosenPmm", "justification", 5, 4L, true, 1L);
    }

    public static List<VoteResponse> getVotes() {
      return Arrays.asList(new VoteResponse(1L, "chosenPmm", "justification", 5, 4L, true, 1L));
    }

  }

  public static class SurveyAnswerSamples {
    public static SurveyAnswerRequest getSurveyAnswerRequestModel() {
      return new SurveyAnswerRequest(1L, 1L, AnswerType.FREE_ANSWER, "the answer of the question");
    }

    public static SurveyAnswerResponse getSurveyAnswerResponseModel() {
      return new SurveyAnswerResponse(1L, 1L, "the answer of the question");
    }
  }

  public static class ProjectSamples {

    public static List<Project> getProjects() {
      Project p1 = getProject();
      Project p2 = getProject();
      p2.setProjectId(2l);
      p2.setCreatedAt(Timestamp.from(Instant.parse("2024-01-02T00:00:00Z")));
      return Arrays.asList(p1, p2);
    }

    public static Project getProject() {
      Project project = Project.builder()
        .projectUserPermissions(new ArrayList<>())
        .projectId(1L)
        .name("projectName")
        .description("this is a test project")
        .folderPath("/test/folder/path")
        .chefId(1L)
        .confirmationState("confirmed")
        .isArchived(true)
        .formProgressStatus(FormProgressStatus.None)
        .createdAt(Timestamp.from(Instant.parse("2024-01-01T00:00:00Z")))
        .updatedAt(Timestamp.from(Instant.parse("2024-01-01T00:00:00Z")))
        .attachments(new ArrayList<>())
        .multiCriteriaAnalysisList(new ArrayList<>())
        .reports(new ArrayList<>())
        .notifications(new ArrayList<>())
        .build();
      ProjectUserPermission projectUserPermission = new ProjectUserPermission();
      projectUserPermission.setUser(UserSamples.getUser());
      projectUserPermission.setProject(project);
      project.setProjectUserPermissions(Arrays.asList(projectUserPermission));
      return project;
    }
  }
  public static class PasswordChangeRequestSamples {

    public static PasswordChangeRequest getPasswordChangeRequest() {
      return new PasswordChangeRequest("Password1@", "Password2@", "Password2@");
    }
  }
  public static class SurveyQuestionSamples {
    public static SurveyQuestion getSurveyQuestion() {
      return new SurveyQuestion(1L, "is this a question", "yes/no question");
    }

    public static SurveyQuestion getSurveyQuestion2() {
      SurveyQuestion question = new SurveyQuestion();
      question.setQuestion("is this a question");
      question.setType("yes/no question");
      return question;
    }

    public static SurveyQuestionRequest getSurveyQuestionRequestModel() {
      return new SurveyQuestionRequest("is this a question", "yes/no question");
    }
  }

  public static class ReportSamples {
    public static List<ReportResponse> getReportResponseModels() {
      return List.of(new ReportResponse(1L, "title", "type1", 1, null ));
    }

    public static Report getReport() {
      Report report = new Report();
      report.setReportId(1L);
      report.setType("type1");
      report.setIteration(1);
      report.setAnalyseId(1L);
      report.setReportPath("pathtoreport");
      return report;
    }
  }

  public static class FormStepSamples {
    public static FormStep getFormStep() {
      FormStep formStep = new FormStep();
      formStep.setStepName("step1");
      formStep.setStepId(1l);
      formStep.setMultiCriteriaAnalysis(ModelSamples.MultiCriteriaAnalysisModel.getMultiCriteriaAnalysis());
      return formStep;
    }

    public static MultiCriteriaStepRequest getFormStepMultiCreteria() {
      return new MultiCriteriaStepRequest(1l, "step2", 2f, null, null);
    }
  }

  public static class MultiCriteriaAnalysisModel {
    public static MultiCriteriaAnalysis getMultiCriteriaAnalysis() {
     MultiCriteriaAnalysis multiCriteriaAnalysis = new MultiCriteriaAnalysis();
     multiCriteriaAnalysis.setMultiCriteriaAnalysisId(1L);

     return multiCriteriaAnalysis;
    }

  }
}
