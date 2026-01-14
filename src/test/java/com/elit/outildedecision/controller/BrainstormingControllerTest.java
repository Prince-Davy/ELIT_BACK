package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.exception.*;
import com.elit.outildedecision.exception.handler.RestResponseEntityExceptionHandler;
import com.elit.outildedecision.model.BrainstormingModel;
import com.elit.outildedecision.repository.BrainstormingRepository;
import com.elit.outildedecision.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.security.CustomMethodSecurityExpressionRoot;
import com.elit.outildedecision.service.implementation.BrainstormingServiceImpl;
import com.elit.outildedecision.util.Constants;
import com.elit.outildedecision.util.ModelSamples;
import com.elit.outildedecision.util.ProjectPermession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.elit.outildedecision.util.Constants.ANALYSIS_DOESNT_EXIST;
import static com.elit.outildedecision.util.Constants.BRAINSTORMING_DOESNT_EXIST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
class BrainstormingControllerTest {

  private MockMvc mockMvc;
  @Mock
  private BrainstormingServiceImpl brainstormingService;

  @Mock
  private BrainstormingRepository brainstormingRepository;
  @Mock
  private PredictibilityAnalysisRepository predictibilityAnalysisRepository;

  @Mock
  CustomMethodSecurityExpressionRoot customMethodSecurity;
  @InjectMocks
  BrainstormingController  brainstormingController;

  private BrainstormingModel brainstormingModel;


  private User user;
  private Project project;

  private Long predictibilityAnalysisId;

  private ProjectUserPermission permission;


  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
    mockMvc = standaloneSetup(brainstormingController).setControllerAdvice(RestResponseEntityExceptionHandler.class).build();

    user = new User();
    user.setUserEmail("user@user.com");
    project = new Project();
    project.setProjectId(1L);
    permission = new ProjectUserPermission();
    permission.setProject(project);
    permission.setUser(user);
    permission.setPermission(ProjectPermession.CONTRIBUTER.toString());
    user.setUserProjectPermissions(List.of(permission));

    predictibilityAnalysisId = 1L;


    brainstormingModel = new BrainstormingModel();
    brainstormingModel.setBrainstormingId(1L);
    brainstormingModel.setBrainstormingText("text text");
  }

  /**
   * Tests of the modified endPoints
   */
  @Test
  void shouldgetBrainStorming_success() throws Exception {
      when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);
      when(brainstormingService.getBrainstorming(project.getProjectId(), predictibilityAnalysisId)).thenReturn(ResponseEntity.ok().body(""));

    mockMvc.perform(get("/brainstorming/"+project.getProjectId()+"/"+ predictibilityAnalysisId))
        .andDo(print())
          .andExpect(status().isOk());
  }
  @Test
  void shouldgetBrainStorming_throwProjectNotFound() throws Exception {
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);
    when(brainstormingService.getBrainstorming(project.getProjectId(), predictibilityAnalysisId)).thenThrow(new ProjectNotFoundException(Constants.PROJECT_DOESNT_EXIST));

    mockMvc.perform(get("/brainstorming/"+project.getProjectId()+"/"+ predictibilityAnalysisId))
      .andDo(print())
      .andExpect(status().isNotFound());
  }  @Test
  void shouldgetBrainStorming_throwPredictibilityAnalysisNotFoundException() throws Exception {
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);
    when(brainstormingService.getBrainstorming(project.getProjectId(), predictibilityAnalysisId)).thenThrow(new PredictibilityAnalysisNotFoundException(ANALYSIS_DOESNT_EXIST));

    mockMvc.perform(get("/brainstorming/"+project.getProjectId()+"/"+ predictibilityAnalysisId))
      .andDo(print())
      .andExpect(status().isNotFound());
  }

  @Test
  void shouldgetBrainStormingAbstract_success() throws Exception {
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);
    when(brainstormingService.getBrainstorming(project.getProjectId(), predictibilityAnalysisId)).thenReturn(ResponseEntity.ok().body(""));

    mockMvc.perform(get("/brainstorming/abstract/"+project.getProjectId()+"/"+ predictibilityAnalysisId))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  void shouldcreateBrainStorming_success() throws Exception {
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);
    when(brainstormingService.createBrainstorming(project.getProjectId(), brainstormingModel)).thenReturn(ResponseEntity.ok().body("Le commentaire du brainstorming a bien été enregistré."));

    mockMvc.perform(post("/brainstorming/"+project.getProjectId()).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(ModelSamples.toJson(brainstormingModel)))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  void shouldcreateBrainStorming_ThrowPredictibilityAnalysisNotFoundException() throws Exception {
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);
    when(brainstormingService.createBrainstorming(project.getProjectId(), brainstormingModel)).thenThrow(new PredictibilityAnalysisNotFoundException(ANALYSIS_DOESNT_EXIST));

    mockMvc.perform(post("/brainstorming/"+project.getProjectId()).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(ModelSamples.toJson(brainstormingModel)))
      .andDo(print())
      .andExpect(status().isNotFound());
  }

  @Test
  void shouldcreateBrainStorming_ThrowExistsBrainStorming() throws Exception {
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);
    when(brainstormingService.createBrainstorming(project.getProjectId(), brainstormingModel)).thenThrow(new ExistingBrainstormingException("Cet utilisateur à déjà soumis un commentaire."));

    mockMvc.perform(post("/brainstorming/"+project.getProjectId()).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(ModelSamples.toJson(brainstormingModel)))
      .andDo(print())
      .andExpect(status().isConflict());
  }

  @Test
  void shouldupdateBrainStorming_success() throws Exception {
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);
    when(brainstormingService.updateBrainstorming(project.getProjectId(), brainstormingModel)).thenReturn(ResponseEntity.ok().body("Le commentaire du brainstorming a bien été enregistré."));

    mockMvc.perform(put("/brainstorming/"+project.getProjectId()).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(ModelSamples.toJson(brainstormingModel)))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  void shouldupdateBrainStorming_ThrowBrainStormingNotFound() throws Exception {
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);
    when(brainstormingService.updateBrainstorming(project.getProjectId(), brainstormingModel)).thenThrow(new BrainstormingNotFoundException(Constants.BRAINSTORMING_DOESNT_EXIST));

    mockMvc.perform(put("/brainstorming/"+project.getProjectId()).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(ModelSamples.toJson(brainstormingModel)))
      .andDo(print())
      .andExpect(status().isNotFound());
  }

  @Test
  void shouldRemoveBrainStorming_Success() throws Exception {
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);
    when(brainstormingService.deleteBrainstorming(project.getProjectId(), brainstormingModel.getBrainstormingId())).thenReturn(ResponseEntity.ok().body("Le commentaire du brainstorming a bien été supprimé."));

    mockMvc.perform(delete("/brainstorming/"+project.getProjectId()+"/"+brainstormingModel.getBrainstormingId()))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  void shouldRemoveBrainStorming_throwBrainstormingNotFoundException() throws Exception {
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);
    when(brainstormingService.deleteBrainstorming(project.getProjectId(), brainstormingModel.getBrainstormingId())).thenThrow(new BrainstormingNotFoundException(BRAINSTORMING_DOESNT_EXIST));

    mockMvc.perform(delete("/brainstorming/"+project.getProjectId()+"/"+brainstormingModel.getBrainstormingId()))
      .andDo(print())
      .andExpect(status().isNotFound());
  }

  @Test
  void shouldValidateBrainStorming_Success() throws Exception {
    HttpEntity<String> isChecked = new HttpEntity<>("true");
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);
    when(brainstormingService.validateOrRejectBrainstorming(project.getProjectId(), brainstormingModel.getBrainstormingId(), isChecked)).thenReturn(ResponseEntity.ok().body("Le commentaire a bien été enregistré."));

    mockMvc.perform(put("/brainstorming/validate/"+project.getProjectId()+"/"+brainstormingModel.getBrainstormingId()).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(String.valueOf(isChecked)) ).andDo(print())
      .andDo(print())
      .andExpect(status().isOk());
  }

}
