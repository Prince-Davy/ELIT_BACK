package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Opinion;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.OpinionAlreadyExistException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.OpinionModel;
import com.elit.outildedecision.service.OpinionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OpinionController {
    private final OpinionService opinionService;

    public OpinionController(OpinionService opinionService) {
        this.opinionService = opinionService;
    }

    @PostMapping("/opinion")
    public ResponseEntity<String> createOpinion(@RequestBody OpinionModel opinionModel) throws UserNotFoundException, OpinionAlreadyExistException {
        return opinionService.createOpinion(opinionModel);
    }

    @GetMapping("/opinion/user")
    public Boolean getOpinionIsAlreadyExist() {
        return opinionService.getOpinionIsAlreadyExist();
    }

}
