package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Brainstorming;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.entity.VerificationToken;
import com.elit.outildedecision.model.UserResponseModel;

import java.util.ArrayList;
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

    public static UserResponseModel createResponseModel(Long userId, String username,
                                                         String userLastName, String userFirstName, String userEmail,
                                                         String userJob, byte[] userAvatar) {
      return new UserResponseModel(userId, username, userLastName,
                                                                 userFirstName, userEmail, userJob,
                                                                 userAvatar);

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

}
