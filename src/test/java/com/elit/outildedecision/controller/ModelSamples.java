package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Brainstorming;
import com.elit.outildedecision.entity.SurveyQuestion;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.entity.VerificationToken;
import com.elit.outildedecision.model.UserModel;
import com.elit.outildedecision.model.UserResponseModel;
import com.elit.outildedecision.model.VoteModelRequest;
import com.elit.outildedecision.model.VoteModelResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

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
}
