package com.elit.outildedecision.shared.user.controller;

import com.elit.outildedecision.shared.exception.BadRequestException;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.user.exception.AvatarNotFoundException;
import com.elit.outildedecision.shared.user.exception.CouldNotSaveUserException;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import com.elit.outildedecision.shared.user.dto.UserResponse;
import com.elit.outildedecision.shared.user.service.implementation.UserServiceImpl;
import com.elit.outildedecision.shared.util.Constants;
import com.elit.outildedecision.shared.util.ModelSamples;
import com.elit.outildedecision.shared.util.ModelSamples.UserSamples;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @MockitoBean
  private UserServiceImpl userService;

  @InjectMocks
  UserController userController;

  private List<UserResponse> userResponseModelList;
  private UserResponse userResponseModel;
  private User user;
  private final String newPassword = "newPass";
  private final String userFirstName = "userFirstName";
  private final String userLastName = "userLastName";

  @BeforeEach
  public void setup() {
    userResponseModelList = Arrays.asList(ModelSamples.UserSamples.getUserResponse());
    userResponseModel = UserSamples.getUserResponse();
    user =  UserSamples.getUser();
  }

  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
  void findUserByIdExists() throws Exception {
    userResponseModel = UserSamples.getUserResponse();
    when(userService.getAllInfosUserById(userResponseModel.getUserId())).thenReturn(userResponseModel);
    mockMvc.perform(get("/user/" + userResponseModel.getUserId())).andDo(print()).andExpect(status().isOk());
  }

  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
  void findUserByIdNotExists() throws Exception {
    when(userService.getAllInfosUserById(UserSamples.notExistUserId)).thenReturn(null);
    mockMvc.perform(get("/user/"+UserSamples.notExistUserId)).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void findUserByKeyWord_Success() throws Exception {
    userResponseModelList = Arrays.asList(ModelSamples.UserSamples.getUserResponse());
    when(userService.findUsersByKeyword("username")).thenReturn(userResponseModelList);
    mockMvc.perform(get("/user/users")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"},username = "user@user.com")
  void shouldUpdatePasswordById_Success() throws Exception {
    when(userService.updatePasswordUserById(eq(user.getUserId()), eq(user.getUserPassword()), eq(newPassword), eq("user@user.com")))
      .thenReturn("Successful Update");
    mockMvc.perform(put("/user/password/"+user.getUserId()).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).param("oldPass", user.getUserPassword()).param("newPass", newPassword))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Successful Update")));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"},username = "user@user.com")
  void shouldUpdatePasswordById_LastPwdNotCorrect () throws Exception {
    when(userService.updatePasswordUserById(eq(user.getUserId()), eq("user"), eq(newPassword), eq("user@user.com")))
      .thenThrow(new BadRequestException("L'ancien mot de passe renseigné n'est pas correct."));
    mockMvc.perform(put("/user/password/"+user.getUserId()).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).param("oldPass", "user").param("newPass", newPassword))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().string(containsString("L'ancien mot de passe renseigné n'est pas correct.")));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"},username = "user@user.com")
  void shouldUpdatePasswordById_PwdOldAndNewAreSame () throws Exception {
    when(userService.updatePasswordUserById(eq(user.getUserId()), eq(user.getUserPassword()), eq(user.getUserPassword()), eq("user@user.com")))
      .thenThrow(new BadRequestException("Le nouveau mot de passe renseigné ne doit pas être identique au précédent."));
    mockMvc.perform(put("/user/password/"+user.getUserId()).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).param("oldPass", user.getUserPassword()).param("newPass", user.getUserPassword()))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().string(containsString("Le nouveau mot de passe renseigné ne doit pas être identique au précédent.")));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void updateAvatarByIdUser_Success() throws Exception {

    HashMap<String, String> contentTypeParams = new HashMap<String, String>();
    MediaType mediaType = new MediaType("multipart", "form-data", contentTypeParams);
    contentTypeParams.put("boundary", "265001916915724");
    MultipartFile file = new MockMultipartFile("userAvatar", "avatar.jpg", "application/json",  "<<avatar data>>".getBytes());

    MockMultipartHttpServletRequestBuilder multipartPut = (MockMultipartHttpServletRequestBuilder) multipart("/user/avatar/1").with(request -> {
      request.setMethod(String.valueOf(HttpMethod.PUT));
      return request;
    });

    when(userService.updateAvatarByIdUser(user.getUserId(), file)).thenReturn("Votre photo a bien été modifiée.");
    mockMvc.perform(multipartPut.file((MockMultipartFile) file).contentType(mediaType))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void updateAvatarByIdUser_ThrowUserNotFoundException() throws Exception {

    HashMap<String, String> contentTypeParams = new HashMap<String, String>();
    MediaType mediaType = new MediaType("multipart", "form-data", contentTypeParams);
    contentTypeParams.put("boundary", "265001916915724");
    MultipartFile file = new MockMultipartFile("userAvatar", "avatar.jpg", "application/json",  "<<avatar data>>".getBytes());

    MockMultipartHttpServletRequestBuilder multipartPut = (MockMultipartHttpServletRequestBuilder) multipart("/api/user/avatar/1").with(request -> {
      request.setMethod(String.valueOf(HttpMethod.PUT));
      return request;
    });

    when(userService.updateAvatarByIdUser(user.getUserId(), file)).thenThrow(new UserNotFoundException("Cet utilisateur n'existe pas!"));
    mockMvc.perform(multipartPut.file((MockMultipartFile) file).contentType(mediaType))
      .andDo(print())
      .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void updateAvatarByIdUser_ThrowCouldNotSaveUserException() throws Exception {

    HashMap<String, String> contentTypeParams = new HashMap<String, String>();
    MediaType mediaType = new MediaType("multipart", "form-data", contentTypeParams);
    contentTypeParams.put("boundary", "265001916915724");
    MultipartFile file = new MockMultipartFile("userAvatar", "avatar.jpg", "application/json",  "<<avatar data>>".getBytes());

    MockMultipartHttpServletRequestBuilder multipartPut = (MockMultipartHttpServletRequestBuilder) multipart("/user/avatar/1").with(request -> {
      request.setMethod(String.valueOf(HttpMethod.PUT));
      return request;
    });

    when(userService.updateAvatarByIdUser(user.getUserId(), file)).thenThrow(new CouldNotSaveUserException("Une erreur est survenue lors de l'enregistrement de votre photo."));
    mockMvc.perform(multipartPut.file((MockMultipartFile) file).contentType(mediaType))
      .andDo(print())
      .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUpdateInfosByUserId_Success() throws Exception {
    when(userService.updateInfosByIdUser(user.getUserId(), userFirstName, userLastName)).thenReturn("Successful Update");
    mockMvc.perform(put("/user/info/"+user.getUserId()).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).param("userFirstName", userFirstName).param("userLastName", userLastName))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Successful Update")));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUpdateInfosByUserId_ThrowCouldNotSaveUserException() throws Exception {
    when(userService.updateInfosByIdUser(user.getUserId(), userFirstName, userLastName)).thenThrow(new CouldNotSaveUserException("Erreur lors de l'enregistrement de userFisrtName"));
    mockMvc.perform(put("/user/info/"+user.getUserId()).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).param("userFirstName", userFirstName).param("userLastName", userLastName))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().string(containsString("Erreur lors de l'enregistrement")));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void deleteAvatarByIdUser_Success() throws Exception {
    when(userService.deleteAvatarByIdUser(user.getUserId())).thenReturn("Photo de profil supprimé avec succès");
    mockMvc.perform(delete("/user/avatar/1"))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void deleteAvatarByIdUser_UserNotFound() throws Exception {
    when(userService.deleteAvatarByIdUser(user.getUserId())).thenThrow(new UserNotFoundException(Constants.USER_NOT_FOUND));
    mockMvc.perform(delete("/user/avatar/1"))
      .andDo(print())
      .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void deleteAvatarByIdUser_AvatarNotFound() throws Exception {
    when(userService.deleteAvatarByIdUser(user.getUserId())).thenThrow(new AvatarNotFoundException(Constants.AVATAR_NOT_FOUND));
    mockMvc.perform(delete("/user/avatar/1"))
      .andDo(print())
      .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void viewAvatarByIdUser_AvatarOk() throws Exception {
    mockMvc.perform(get("/user/avatar/1"))
      .andDo(print())
      .andExpect(status().isOk());
  }

}
