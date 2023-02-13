package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.service.SuperAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class SuperAdminController {

    @Autowired
    SuperAdminService superAdminService;

    @PutMapping("/projects/{projectId}")
    public ResponseEntity confirmProject(@PathVariable Long projectId) throws ProjectNotFoundException {
        return superAdminService.confirmProject(projectId);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity suspendUser(@PathVariable Long userId) throws UserNotFoundException {
        return superAdminService.suspendUser(userId);
    }

    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity deleteProject(@PathVariable Long projectId) throws ProjectNotFoundException {
        return superAdminService.deleteProject(projectId);
    }

    /* @GetMapping("/projects/notConfirmed")
    public List<Project> getAllNonConfirmedProjects(){
        return superAdminService.getAllNonConfirmedProjects();
    } */

    @GetMapping("/projects/notConfirmed/{page}")
    public Page<Project> getAllNonConfirmedProjects(@PathVariable int page){
        return superAdminService.getAllNonConfirmedProjects(page);
    }

    @GetMapping("/projects/notConfirmed/{page}/orderByDate")
    public Page<Project> getAllConfirmedProjectsOrderByDate(@PathVariable int page, @RequestParam String order){
        return superAdminService.getAllNonConfirmedProjectsOrderByDate(page, order);
    }

    @PutMapping("/projects/confirmProjects")
    public ResponseEntity confirmProjects(@RequestBody List<Long> projectIds) throws ProjectNotFoundException {
        return superAdminService.confirmProjects(projectIds);
    }

    @GetMapping("/projects/notConfirmed/{page}/search")
    public Page<Project> findNotConfirmedProjectByKeyword(@RequestParam String keyword, @PathVariable int page){
        System.out.println("keyword = " + keyword);
        return superAdminService.findNotConfirmedProjectByKeyword(keyword, page);
    }

}
