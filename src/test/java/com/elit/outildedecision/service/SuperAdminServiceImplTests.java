package com.elit.outildedecision.service;

import com.elit.outildedecision.util.ModelSamples;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.service.implementation.SuperAdminServiceImpl;
import com.elit.outildedecision.util.ProjectConfirmationState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuperAdminServiceImplTests {

  @Mock
  private  ProjectRepository projectRepository;

  @Mock
  private  UserRepository userRepository;
  @InjectMocks
  private SuperAdminServiceImpl superAdminService;

  private int PAGE_SIZE, pageNumber;
  private List<Project> projectList;
  private Project project;
  private User user;


  @BeforeEach
  public void setup() {
    PAGE_SIZE = 10;
    pageNumber = 1;
    projectList = ModelSamples.ProjectSamples.getProjects();
    project = ModelSamples.ProjectSamples.getProject();
    user = ModelSamples.UserSamples.getUser();
  }

  @Test
  void shouldgetAllNonConfirmedProjects_Success() {
      Page<Project> page = new PageImpl<>(projectList);
      when(projectRepository.findAllByConfirmationState(ProjectConfirmationState.WAITING.name(), PageRequest.of(pageNumber, PAGE_SIZE))).thenReturn(new PageImpl<>(projectList));
      Assertions.assertEquals(page, superAdminService.getAllNonConfirmedProjects(pageNumber));
  }

  @Test
  void shouldFindNotConfirmedProjectByKeyword_Success() {
      Page<Project> page = new PageImpl<>(projectList);
      when(projectRepository.findByNameOrDescriptionContainingIgnoreCase(projectList.get(0).getName(), PageRequest.of(pageNumber, PAGE_SIZE))).thenReturn(new PageImpl<>(projectList));
      Assertions.assertEquals(page, superAdminService.findNotConfirmedProjectByKeyword(projectList.get(0).getName(), pageNumber));
  }

  @Test
  void shouldConfirmProject_Success() throws ProjectNotFoundException {
    when(projectRepository.findById(project.getProjectId())).thenReturn(Optional.of(project));
    project.setConfirmationState(ProjectConfirmationState.CONFIRMED.name());
    when(projectRepository.save(project)).thenReturn(project);

    Assertions.assertEquals(ResponseEntity.ok("Votre projet a été confirmé"), superAdminService.confirmProject(project.getProjectId()));
  }

  @Test
  void shouldConfirmProject_ThrowProjectNotFoundException() {
    when(projectRepository.findById(project.getProjectId())).thenReturn(Optional.empty());
    ProjectNotFoundException p = Assertions.assertThrows(ProjectNotFoundException.class, ()-> superAdminService.confirmProject(project.getProjectId()));

    Assertions.assertEquals("Ce projet n'existe pas!", p.getMessage());
  }

  @Test
  void shouldConfirmProjects_Success() throws ProjectNotFoundException {
    when(projectRepository.findById(any())).thenReturn(Optional.of(project));
    when(projectRepository.save(any())).thenReturn(project);

    List<Long> projectIds = projectList.stream().map(e -> e.getProjectId()).collect(Collectors.toList());
    Assertions.assertEquals(ResponseEntity.ok().body("Vos projets sont confirmés"), superAdminService.confirmProjects(projectIds));
  }

  @Test
  void shouldConfirmProjects_ThrowProjectNotFoundException()  {
    when(projectRepository.findById(any())).thenReturn(Optional.empty());

    List<Long> projectIds = projectList.stream().map(e -> e.getProjectId()).collect(Collectors.toList());

    ProjectNotFoundException p = Assertions.assertThrows(ProjectNotFoundException.class, ()-> superAdminService.confirmProjects(projectIds));
    Assertions.assertEquals("Ce projet n'existe pas!", p.getMessage());

  }

  @Test
  void shouldSuspendUser_Success() throws UserNotFoundException {
    when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
    user.setIsUserActive(false);
    when(userRepository.save(user)).thenReturn(user);

    Assertions.assertEquals(ResponseEntity.ok("L'utilisateur a été suspendu"), superAdminService.suspendUser(user.getUserId()));
  }

  @Test
  void shouldSuspendUser_ThrowException() {
    when(userRepository.findById(user.getUserId())).thenReturn(Optional.empty());
    UserNotFoundException u = Assertions.assertThrows(UserNotFoundException.class,()-> superAdminService.suspendUser(user.getUserId()));
    Assertions.assertEquals("Cet utilisateur n'existe pas!", u.getMessage());
  }

  @Test
  void shouldDeleteProject_Success() throws ProjectNotFoundException {
    when(projectRepository.findById(any())).thenReturn(Optional.of(project));
    superAdminService.deleteProject(project.getProjectId());
    Mockito.verify(projectRepository, Mockito.times(1)).delete(project);
  }

  @Test
  void shouldDeleteProject_ThrowProjectNotFoundException() throws ProjectNotFoundException {
    when(projectRepository.findById(any())).thenReturn(Optional.empty());
    Assertions.assertThrows(ProjectNotFoundException.class, () -> superAdminService.deleteProject(project.getProjectId()));
  }

  @Test
  void shouldGetAllConfirmedProjectsOrdredByDate_DESC() {
    Page<Project> pageProject = new PageImpl<>(projectList);
    when(projectRepository.findAllByConfirmationStateOrderByCreatedAtDesc(ProjectConfirmationState.WAITING.name(), PageRequest.of(pageNumber, PAGE_SIZE))).thenReturn(pageProject);
    Assertions.assertEquals(pageProject, superAdminService.getAllNonConfirmedProjectsOrderByDate(pageNumber, "DESC"));
  }

  @Test
  void shouldGetAllConfirmedProjectsOrdredByDate_ASC() {
    Page<Project> pageProject = new PageImpl<>(projectList);
    when(projectRepository.findAllByConfirmationStateOrderByCreatedAtAsc(ProjectConfirmationState.WAITING.name(), PageRequest.of(pageNumber, PAGE_SIZE))).thenReturn(pageProject);
    Assertions.assertEquals(pageProject, superAdminService.getAllNonConfirmedProjectsOrderByDate(pageNumber, "ASC"));
  }
  @Test
  void shouldGetAllConfirmedProjectsOrdredByDate_AllConfirmationState() {
    Page<Project> pageProject = new PageImpl<>(projectList);
    when(projectRepository.findAllByConfirmationState(ProjectConfirmationState.WAITING.name(), PageRequest.of(pageNumber, PAGE_SIZE))).thenReturn(pageProject);
    Assertions.assertEquals(pageProject, superAdminService.getAllNonConfirmedProjectsOrderByDate(pageNumber, ""));
  }
}
