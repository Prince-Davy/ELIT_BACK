package com.elit.outildedecision.controller;

import com.elit.outildedecision.model.UserResponseModel;
import com.elit.outildedecision.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.elit.outildedecision.controller.ModelSamples.UserSamples;

import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTests {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UserServiceImpl userService;

  @InjectMocks
  UserController userController;

  private List<UserResponseModel> userResponseModelList;
  private UserResponseModel userResponseModel;

  @BeforeEach
  public void setup() {
    userResponseModelList = Arrays.asList(ModelSamples.UserSamples.getUserResponseModel());
    userResponseModel = UserSamples.getUserResponseModel();
  }

  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
   void findUserByIdExists() throws Exception {
    userResponseModel = UserSamples.getUserResponseModel();
    when(userService.getAllInfosUserById(userResponseModel.getUserId())).thenReturn(userResponseModel);
    mockMvc.perform(get("/api/user/"+userResponseModel.getUserId())).andDo(print()).andExpect(status().isOk());
  }

  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
   void findUserByIdNotExists() throws Exception {
    when(userService.getAllInfosUserById(UserSamples.notExistUserId)).thenReturn(null);
    mockMvc.perform(get("/api/user/"+UserSamples.notExistUserId)).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void ShouldfindUserByKeyWordEmpty() throws Exception {
    userResponseModelList = Arrays.asList(ModelSamples.UserSamples.getUserResponseModel());
    when(userService.findUsersByKeyword("")).thenReturn(userResponseModelList);
    mockMvc.perform(get("/api/user/users")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void findUserByKeyWordExists() throws Exception {
    userResponseModelList = Arrays.asList(ModelSamples.UserSamples.getUserResponseModel());
    when(userService.findUsersByKeyword("username")).thenReturn(userResponseModelList);
    mockMvc.perform(get("/api/user/users")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void findUserByKeyWordNotExists() throws Exception {
    userResponseModelList = Arrays.asList(ModelSamples.UserSamples.getUserResponseModel());
    when(userService.findUsersByKeyword("username")).thenReturn(userResponseModelList);
    mockMvc.perform(get("/api/user/users")).andDo(print()).andExpect(status().isOk());
  }
}
