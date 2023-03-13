package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Brainstorming;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.entity.VerificationToken;
import com.elit.outildedecision.model.UserModel;
import com.elit.outildedecision.model.UserResponseModel;
import com.elit.outildedecision.model.VoteModelRequest;
import com.elit.outildedecision.model.VoteModelResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelSamples {

  public static class UserSamples {
    static Long notExistUserId = 14L;

    public static User getUser() {
      return createUser(1L, "username", "lastname",
             "firstname", "user@user.com", "developer",
              null, "password", true,
        true,true, new ArrayList<>(), new ArrayList<>());
    }

    public static UserResponseModel getUserResponseModel() {
      return createResponseModel(1L, "username", "lastname",
        "firstname", "user@user.com", "developer",
        null);
    }

    public static UserModel getUserModel() {
      return createUserModel("userNameModel", "lastNameModel", "userFirstNameModel", "userEmailModel@user.com", "developper", "password", "userEmailModel@user.com", "password", null);
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

    public static UserResponseModel createResponseModel(Long userId, String username,
                                                        String userLastName, String userFirstName, String userEmail,
                                                        String userJob, byte[] userAvatar) {
      return new UserResponseModel(userId, username, userLastName,
        userFirstName, userEmail, userJob,
        userAvatar);

    }

    public static UserModel createUserModel(String username, String userLastName, String userFirstName, String userEmail, String userJob, String userPassword, String userEmailConfirmation, String userPasswordConfirmation, MultipartFile userAvatar) {
      return new UserModel(username, userLastName, userFirstName, userEmail, userJob, userPassword, userEmailConfirmation, userPasswordConfirmation, userAvatar);
    }
  }

  public static class VoteSamples{
    public static VoteModelRequest getVoteModelRequest() {
      return craeteVoteModelRequest(1L, "chosenPmm", "justification", 5, 4L,true,1L);
    }
    public static List<VoteModelResponse> getVotes() {
      return Arrays.asList(createVoteModelResponse(1L, "chosenPmm", "justification", 5, 4L,true,1L));
    }

    public static VoteModelRequest craeteVoteModelRequest(Long voteId, String chosenPMM, String justification, Integer predictibilityValue, Long predictibilityAnalysisId, Boolean isConfirmed, Long userId) {
      return  new VoteModelRequest(voteId, chosenPMM,justification,predictibilityValue,predictibilityAnalysisId,isConfirmed,userId);
      }
    public static VoteModelResponse createVoteModelResponse(Long voteId, String chosenPMM, String justification, Integer predictibilityValue, Long predictibilityAnalysisId, Boolean isConfirmed, Long userId) {
      return  new VoteModelResponse(voteId, chosenPMM,justification,predictibilityValue,predictibilityAnalysisId,isConfirmed,userId);
    }
  }
}
