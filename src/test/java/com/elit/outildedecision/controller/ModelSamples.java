package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.model.FormQuestionModel;
import com.elit.outildedecision.model.FormStepModelForBrainstorming;
import com.elit.outildedecision.model.UserModel;
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

    public static UserModel getUserModel() {
      return createUserModel("username", "lastname",
        "firstname", "user@user.com", "developer", "Azerty!123");
    }

    public static UserResponseModel getUserResponseModel() {
      return createResponseModel(1L, "username", "lastname",
        "firstname", "user@user.com", "developer",
        null);
    }

    public static FormStep getCreateFormStep(){
      MultiCriteriaAnalysis multiCriteriaAnalysis = new MultiCriteriaAnalysis();
      return createFormStep(1L,"NameStep", 2L,4F,multiCriteriaAnalysis);
    }

    public static FormStepModelForBrainstorming getFormStepModelForBrainstorming(){
      return createBrainstorming(1L,"StepName",3L, 12F,1L,null);
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
    public static UserModel createUserModel( String username, String userLastName,
                                  String userFirstName, String userEmail, String userJob,
                                  String userPassword
                                 ) {
      {
        UserModel userModel = new UserModel();
        userModel.setUserFirstName(userFirstName);
        userModel.setUserLastName(userLastName);
        userModel.setUsername(username);
        userModel.setUserPassword(userPassword);
        userModel.setUserEmail(userEmail);
        userModel.setUserJob(userJob);
        userModel.setUserEmailConfirmation(userEmail);
        userModel.setUserPasswordConfirmation(userPassword);

        return userModel;
      }
    }

    public static FormStepModelForBrainstorming createBrainstorming(Long stepId, String stepName, Long stepRef, Float eScore, Long multiCriteriaAnalysisId,
    List<FormQuestionModel> formQuestions){
      FormStepModelForBrainstorming modelForBrainstorming = new FormStepModelForBrainstorming();
      modelForBrainstorming.setStepId(stepId);
      modelForBrainstorming.setStepName(stepName);
      modelForBrainstorming.setStepRef(stepRef);
      modelForBrainstorming.setEScore(eScore);
      modelForBrainstorming.setMultiCriteriaAnalysisId(multiCriteriaAnalysisId);
      modelForBrainstorming.setFormQuestions(formQuestions);
      return modelForBrainstorming;
    }

    public static FormStep createFormStep(Long stepId, String stepName, Long stepRef, Float eScore, MultiCriteriaAnalysis multiCriteriaAnalysis ){
      FormStep formStep = new FormStep();
      formStep.setStepId(stepId);
      formStep.setStepName(stepName);
      formStep.setStepRef(stepRef);
      formStep.setEScore(eScore);
      formStep.setMultiCriteriaAnalysis(multiCriteriaAnalysis);

      return null;
    }

  }

}
