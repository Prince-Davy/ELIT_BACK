package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.BrainstormingNotFoundException;
import com.elit.outildedecision.model.BrainstormingModel;
import com.elit.outildedecision.service.BrainstormingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/brainstorming")
public class BrainstormingController {
    private final BrainstormingService brainstormingService;

    public BrainstormingController(BrainstormingService brainstormingService) { this.brainstormingService = brainstormingService; }

    @GetMapping("/{predictibilityAnalysisId}")
    public ResponseEntity<Object> getBrainstorming(@PathVariable Long predictibilityAnalysisId) throws Exception {
        return brainstormingService.getBrainstorming(predictibilityAnalysisId);
    }

    @GetMapping("/abstract/{predictibilityAnalysisId}")
    public ResponseEntity<Object> getBrainstormingAbstract(@PathVariable Long predictibilityAnalysisId) throws Exception {
        return brainstormingService.getBrainstormingAbstract(predictibilityAnalysisId);
    }

    @PostMapping("")
    public ResponseEntity<String> createBrainstorming(@Valid @RequestBody BrainstormingModel brainstormingModel) throws Exception {
        return brainstormingService.createBrainstorming(brainstormingModel);
    }

    @PutMapping("")
    public  ResponseEntity<String> updateBrainstorming( @RequestBody BrainstormingModel brainstormingModel) throws Exception {
        return brainstormingService.updateBrainstorming(brainstormingModel);
    }

    @DeleteMapping("/{brainstormingId}")
    public ResponseEntity<String> removeBrainstorming(@PathVariable Long brainstormingId) throws BrainstormingNotFoundException {
        return brainstormingService.deleteBrainstorming(brainstormingId);
    }

    @PutMapping("/validate/{brainstormingId}")
    public ResponseEntity<String> validateOrRejectBrainstorming(@PathVariable Long brainstormingId, HttpEntity<String> isChecked) throws BrainstormingNotFoundException, JsonProcessingException {
        return brainstormingService.validateOrRejectBrainstorming(brainstormingId,isChecked);
    }


}
