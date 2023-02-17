package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.service.SuperAdminService;
import com.elit.outildedecision.util.ProjectConfirmationState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class SuperAdminServiceImpl implements SuperAdminService {

  @Autowired ProjectRepository projectRepository;
  @Autowired UserRepository userRepository;

  /**
   * @param projectId
   * @return ResponseEntity
   * @throws ProjectNotFoundException
   */
  @Override
  public ResponseEntity confirmProject(Long projectId) throws ProjectNotFoundException {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("Ce projet n'existe pas!"));
    project.setConfirmationState(ProjectConfirmationState.CONFIRMED.name());
    projectRepository.save(project);
    return ResponseEntity.ok("Votre projet a été confirmé");
  }

  @Override
  public ResponseEntity suspendUser(Long userId) throws UserNotFoundException {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("Cet utilisateur n'existe pas!"));
    user.setIsUserActive(false);
    userRepository.save(user);
    return ResponseEntity.ok("L'utilisateur a été suspendu");
  }

  @Override
  public ResponseEntity deleteProject(Long projectId) throws ProjectNotFoundException {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("Ce projet n'existe pas!"));
    projectRepository.delete(project);
    return ResponseEntity.ok("Le projet a été supprimé");
  }

  @Override
  public List<Project> getAllNonConfirmedProjects() {
    List<Project> projects =
        projectRepository.findAllByConfirmationState(ProjectConfirmationState.WAITING.name());
    return projects;
  }

  @Override
  public Page<Project> getAllNonConfirmedProjects(int page) {
    return projectRepository.findAllByConfirmationState(
        ProjectConfirmationState.WAITING.name(), PageRequest.of(page, 10));
  }

  @Override
  public Page<Project> getAllNonConfirmedProjectsOrderByDate(int page, String order) {
    switch (order) {
      case "DESC":
        return projectRepository.findAllByConfirmationStateOrderByCreatedAtDesc(
            ProjectConfirmationState.WAITING.name(), PageRequest.of(page, 10));
      case "ASC":
        return projectRepository.findAllByConfirmationStateOrderByCreatedAtAsc(
            ProjectConfirmationState.WAITING.name(), PageRequest.of(page, 10));
      default:
        return projectRepository.findAllByConfirmationState(
            ProjectConfirmationState.WAITING.name(), PageRequest.of(page, 10));
    }
  }

  @Override
  public ResponseEntity confirmProjects(List<Long> projectIds) throws ProjectNotFoundException {
    for (Long id : projectIds) {
      confirmProject(id);
    }
    return ResponseEntity.ok().body("Vos projets sont confirmés");
  }

  @Override
  public Page<Project> findNotConfirmedProjectByKeyword(String keyword, int page) {
    Page<Project> projects =
        projectRepository.findByNameOrDescriptionContainingIgnoreCase(
            keyword, PageRequest.of(page, 10));
    System.out.println("projects = " + projects);
    return projects;
  }
}
