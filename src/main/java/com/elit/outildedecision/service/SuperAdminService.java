package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SuperAdminService {
    ResponseEntity confirmProject(Long projectId) throws ProjectNotFoundException;

    ResponseEntity suspendUser(Long userId) throws UserNotFoundException;

    ResponseEntity deleteProject(Long projectId) throws ProjectNotFoundException;

    List<Project> getAllNonConfirmedProjects();

    Page<Project> getAllNonConfirmedProjects(int page);

    Page<Project> getAllNonConfirmedProjectsOrderByDate(int page, String order);

    ResponseEntity confirmProjects(List<Long> projectIds) throws ProjectNotFoundException;

    Page<Project> findNotConfirmedProjectByKeyword(String keyword, int page);
}
