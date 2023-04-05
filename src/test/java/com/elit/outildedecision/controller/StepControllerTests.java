package com.elit.outildedecision.controller;


import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.exception.StepFormNotFoundException;
import com.elit.outildedecision.model.MultiCriteriaStepModel;
import com.elit.outildedecision.service.FormStepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StepControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  FormStepService formStepService;

  @InjectMocks
  StepController stepController;

  private  Long formId;

  private FormStep formStep;
  private MultiCriteriaStepModel formStepMultiCreteria;

  @BeforeEach
  public void setUp() {
    formStep = ModelSamples.FormStepSamples.getFormStep();
    formStepMultiCreteria = ModelSamples.FormStepSamples.getFormStepMultiCreteria();
    formId = 1l;
  }

  @Test
  @WithMockUser(authorities = "USER")
  void shouldGetFormStep_Success() throws Exception {
    when(formStepService.findStepByNameAndFormId(formId,formStep.getStepName())).thenReturn(formStep);
    mockMvc.perform(get("/steps/"+formId +"/"+ formStep.getStepName()))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().json(ModelSamples.toJson(formStep)));
  }

  @Test
  @WithMockUser(authorities = "USER")
  void shouldGetFormStep_ThrowStepFormNotFoundException() throws Exception {
    when(formStepService.findStepByNameAndFormId(formId,formStep.getStepName())).thenThrow(new StepFormNotFoundException("This Step Doesn't Exist!"));
    mockMvc.perform(get("/steps/"+formId +"/"+ formStep.getStepName()))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value("NOT_FOUND"))
      .andExpect(jsonPath("$.message").value("This Step Doesn't Exist!"));;
  }

  @Test
  @WithMockUser(authorities = "USER")
  void shouldUpdateFormStep_Success() throws Exception {
    when(formStepService.updateFormStep(formStepMultiCreteria)).thenReturn(ResponseEntity.status(HttpStatus.OK).body("Vos Modifications ont été enregistrées !"));
    mockMvc.perform(MockMvcRequestBuilders.put("/steps").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(ModelSamples.toJson(formStepMultiCreteria)))
      .andDo(print())
      .andExpect(status().isOk());
  }

}
