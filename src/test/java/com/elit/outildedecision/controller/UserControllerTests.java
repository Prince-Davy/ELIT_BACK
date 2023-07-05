package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.AvatarNotFoundException;
import com.elit.outildedecision.exception.CouldNotSaveUserException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.UserResponseModel;
import com.elit.outildedecision.service.implementation.UserServiceImpl;
import com.elit.outildedecision.util.Constants;
import com.elit.outildedecision.util.ModelSamples;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.elit.outildedecision.util.ModelSamples.UserSamples;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTests {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UserServiceImpl userService;

  @InjectMocks
  UserController userController;

  private List<UserResponseModel> userResponseModelList;
  private UserResponseModel userResponseModel;
  private User user;
  private final String newPassword = "newPass";
  private final String userFirstName = "userFirstName";
  private final String userLastName = "userLastName";

  @BeforeEach
  public void setup() {
    userResponseModelList = Arrays.asList(ModelSamples.UserSamples.getUserResponseModel());
    userResponseModel = UserSamples.getUserResponseModel();
    user =  UserSamples.getUser();
  }

  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
  void findUserByIdExists() throws Exception {
    userResponseModel = UserSamples.getUserResponseModel();
    when(userService.getAllInfosUserById(userResponseModel.getUserId())).thenReturn(userResponseModel);
    mockMvc.perform(get("/user/"+userResponseModel.getUserId())).andDo(print()).andExpect(status().isOk());
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
    userResponseModelList = Arrays.asList(ModelSamples.UserSamples.getUserResponseModel());
    when(userService.findUsersByKeyword("username")).thenReturn(userResponseModelList);
    mockMvc.perform(get("/user/users")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUpdatePasswordById_Success() throws Exception {
    when(userService.updatePasswordUserById(user.getUserId(), user.getUserPassword(), newPassword)).thenReturn(ResponseEntity.status(HttpStatus.OK).body("Successful Update"));
    mockMvc.perform(put("/user/password/"+user.getUserId()).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).param("oldPass", user.getUserPassword()).param("newPass", newPassword))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Successful Update")));
  }
  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUpdatePasswordById_LastPwdNotCorrect () throws Exception {
    when(userService.updatePasswordUserById(user.getUserId(), "user", newPassword)).thenReturn(ResponseEntity.badRequest().body("L'ancien mot de passe renseigné n'est pas correct."));
    mockMvc.perform(put("/user/password/"+user.getUserId()).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).param("oldPass", "user").param("newPass", newPassword))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().string(containsString("L'ancien mot de passe renseigné n'est pas correct.")));
  }
  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUpdatePasswordById_PwdOldAndNewAreSame () throws Exception {
    when(userService.updatePasswordUserById(user.getUserId(), user.getUserPassword(), user.getUserPassword())).thenReturn(ResponseEntity.badRequest().body("Le nouveau mot de passe renseigné ne doit pas être identique au précédent."));
    mockMvc.perform(put("/user/password/"+user.getUserId()).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).param("oldPass", user.getUserPassword()).param("newPass", user.getUserPassword()))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().string(containsString("Le nouveau mot de passe renseigné ne doit pas être identique au précédent.")));
  }

 /*@Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUpdateAvatar_Success() throws Exception {
    MultipartFile file = new MockMultipartFile("file", "avatar.png", MediaType.IMAGE_JPEG_VALUE,  "<<avatar data>>".getBytes());
    when(userService.updateAvatarByIdUser(user.getUserId(), file)).thenReturn(ResponseEntity.status(HttpStatus.OK).body("Votre Photo de profil a été modifiée avec succès"));
    mockMvc.perform(MockMvcRequestBuilders.multipart("/user/avatar/"+user.getUserId()).file((MockMultipartFile) file).accept(MediaType.MULTIPART_FORM_DATA).contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
      .andDo(print())
      .andExpect(status().isOk());
  }*/

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

    when(userService.updateAvatarByIdUser(user.getUserId(), file)).thenReturn(ResponseEntity.status(HttpStatus.OK).body("Votre photo a bien été modifiée."));
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
    when(userService.updateInfosByIdUser(user.getUserId(), userFirstName, userLastName)).thenReturn(ResponseEntity.status(HttpStatus.OK).body("Successful Update"));
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
    when(userService.deleteAvatarByIdUser(user.getUserId())).thenReturn(ResponseEntity.ok().body("Photo de profil supprimé avec succès"));
    mockMvc.perform(delete("/user/avatar/1"))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void deleteAvatarByIdUser_ErrorDelete() throws Exception {
    when(userService.deleteAvatarByIdUser(user.getUserId())).thenReturn(ResponseEntity.badRequest().body("Erreur lors de la suppression de la photo de profil."));
    mockMvc.perform(delete("/user/avatar/1"))
      .andDo(print())
      .andExpect(status().isBadRequest());
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
}
