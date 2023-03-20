package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.model.*;
import com.elit.outildedecision.util.AnswerType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
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
    static Long notExistUserId = 14L;

    public static UserResponseModel getUserResponseModel() {
      return new UserResponseModel(1L, "username", "lastname",
        "firstname", "user@user.com", "developer",
        null);
    }

    public static User getUser () {
      return createUser(1L, "username", "userLastName", "userFirstName",
                               "user@user.com", "developper", "useravatarPath", "userPassword", true,true, true,
        new ArrayList<VerificationToken>(), new ArrayList<Brainstorming>());
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
        user.setUserJob(userJob);
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

  public static class VoteSamples{
    public static VoteModelRequest getVoteModelRequest() {
      return new VoteModelRequest(1L, "chosenPmm", "justification", 5, 4L,true,1L);
    }
    public static List<VoteModelResponse> getVotes() {
      return Arrays.asList(new VoteModelResponse(1L, "chosenPmm", "justification", 5, 4L,true,1L));
    }

  }

  public static class SurveyQuestionSamples {
    public static SurveyQuestion getSurveyQuestion() {
      return new SurveyQuestion(1L, "is this a question", "yes/no question");
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

    public  static List<Project> getProjects() {
      Project p1 = getProject();
      Project p2 = getProject();
      p2.setProjectId(2l);
      p2.setCreatedAt(Timestamp.from(Instant.now().minus(1, ChronoUnit.DAYS)));
      System.out.println(p1.getCreatedAt());
      System.out.println(p2.getCreatedAt());
      return Arrays.asList(p1, p2);
    }
    public static Project getProject() {
      Project project = new Project(new ArrayList<>(), 1L, "projectName", "this is a test project", "/test/folder/path", 1L, "confirmed", true, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()) ;
      ProjectUserPermission projectUserPermission = new ProjectUserPermission();
      projectUserPermission.setUser(UserSamples.getUser());
      projectUserPermission.setProject(project);
      project.setProjectUserPermissions(Arrays.asList(projectUserPermission));
      return  project;
      }
  }

  public static class PasswordChangeModelSamples {

    public static PasswordChangeModel getPasswordChangeModel() {
      return  new PasswordChangeModel("Password1@", "Password2@", "Password2@");
    }
  }
}
