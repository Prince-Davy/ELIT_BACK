package com.elit.outildedecision.util;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.enumeration.AnswerType;
import com.elit.outildedecision.enumeration.FormProgressStatus;
import com.elit.outildedecision.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.elit.outildedecision.model.UserModel;
import com.elit.outildedecision.model.UserResponseModel;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    public static UserResponseModel getUserResponseModel() {
      return new UserResponseModel(1L, "username", "lastname",
        "firstname", "user@user.com",
        null);
    }

    public static UserModel createUserModel(String username, String userLastName,
                                            String userFirstName, String userEmail,
                                            String userPassword, String userPasswordConfirmation) {
      UserModel userModel = new UserModel();
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
        "user@user.com", "developper", "useravatarPath", "userPassword", true, true, true,
        new ArrayList<VerificationToken>(), new ArrayList<Brainstorming>());
    }
    public static User getUser2() {
      return createUser(1L, "user", "Doe", "John",
        "user2@user.com", "Développeur front", null, "user", true, true, false,
        null, null);
    }

    public static UserModel getUserModel() {
      return createUserModel("username", "lastname",
        "firstname", "user@user.com", "developer", "Azerty!123");
    }


    public static UserResponseModel createResponseModel(Long userId, String username,
                                                        String userLastName, String userFirstName, String userEmail,
                                                        byte[] userAvatar) {
      return new UserResponseModel(userId, username, userLastName,
        userFirstName, userEmail, userAvatar);

    }

    public static User createUser(Long userId, String username, String userLastName,
                                  String userFirstName, String userEmail, String userJob,
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
    public static VoteModelRequest getVoteModelRequest() {
      return new VoteModelRequest(1L, "chosenPmm", "justification", 5, 4L, true, 1L);
    }

    public static List<VoteModelResponse> getVotes() {
      return Arrays.asList(new VoteModelResponse(1L, "chosenPmm", "justification", 5, 4L, true, 1L));
    }

  }

  public static class SurveyAnswerSamples {
    public static SurveyAnswerRequestModel getSurveyAnswerRequestModel() {
      SurveyAnswerRequestModel surveyAnswerRequestModel = new SurveyAnswerRequestModel();
      surveyAnswerRequestModel.setUserId(1L);
      surveyAnswerRequestModel.setQuestionId(1L);
      surveyAnswerRequestModel.setAnswerType(AnswerType.FREE_ANSWER);
      surveyAnswerRequestModel.setAnswerValue("the answer of the question");
      return surveyAnswerRequestModel;
    }

    public static SurveyAnswerResponseModel getSurveyAnswerResponseModel() {
      SurveyAnswerResponseModel surveyAnswerResponseModel = new SurveyAnswerResponseModel();
      surveyAnswerResponseModel.setAnswer("the answer of the question");
      surveyAnswerResponseModel.setQuestionId(1L);
      surveyAnswerResponseModel.setUserId(1L);
      return surveyAnswerResponseModel;
    }
  }

  public static class ProjectSamples {

    public static List<Project> getProjects() {
      Project p1 = getProject();
      Project p2 = getProject();
      p2.setProjectId(2l);
      p2.setCreatedAt(Timestamp.from(Instant.now().minus(1, ChronoUnit.DAYS)));
      System.out.println(p1.getCreatedAt());
      System.out.println(p2.getCreatedAt());
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
        .createdAt(new Timestamp(System.currentTimeMillis()))
        .updatedAt(new Timestamp(System.currentTimeMillis()))
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
  public static class PasswordChangeModelSamples {

    public static PasswordChangeModel getPasswordChangeModel() {
      return new PasswordChangeModel("Password1@", "Password2@", "Password2@");
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

    public static SurveyQuestionModel getSurveyQuestionRequestModel() {
      return new SurveyQuestionModel("is this a question", "yes/no question");
    }
  }

  public static class ReportSamples {
    public static List<ReportResponseModel> getReportResponseModels() {
      return List.of(new ReportResponseModel(1L, "title", "type1", 1, null ));
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

    public static MultiCriteriaStepModel getFormStepMultiCreteria() {
      return new MultiCriteriaStepModel(1l, "step2", 2f, null, null);
    }
  }

  public static class MultiCriteriaAnalysisModel {
    public static MultiCriteriaAnalysis getMultiCriteriaAnalysis() {
     MultiCriteriaAnalysis multiCriteriaAnalysis = new MultiCriteriaAnalysis();
     multiCriteriaAnalysis.setMultiCriteriaAnalysisId(1L);

     return multiCriteriaAnalysis;
    }

  }
  public static class OpinionSamples {
    public static Opinion getOpinion() {
      Opinion opinion = new Opinion();
      opinion.setOpinionId(1L);
      opinion.setRating(1);
      opinion.setDescription("opinion1");
      opinion.setUser(ModelSamples.UserSamples.getUser());

      return opinion;
    }

    public static OpinionModel getOpinionModel() {
      OpinionModel opinionModel = new OpinionModel(1, "opinion1");
      return opinionModel;
    }
  }
}
