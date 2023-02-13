package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.PredictibilityAnaliseNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.model.PredictibilityScoreModel;
import com.elit.outildedecision.service.PredictibilityAnalysisService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class PredictibilityAnalysisController {

    private final PredictibilityAnalysisService predictibilityAnalysisService;

    public PredictibilityAnalysisController(PredictibilityAnalysisService predictibilityAnalysisService) {
        this.predictibilityAnalysisService = predictibilityAnalysisService;
    }

    @PostMapping("/predictibility-analysis/{projectId}")
    //@PreAuthorize("isProjectOwner(#projectId)")
    ResponseEntity<Long> createPredictibilityAnalysis(@PathVariable Long projectId){
        return predictibilityAnalysisService.createPredictibilityForm(projectId);
    }
/*
    @PostMapping("/predictibility-analysis2/{projectId}")
    ResponseEntity<String> createPredictibilityAnalysis2(@PathVariable Long projectId) throws IOException, ParseException, org.json.simple.parser.ParseException{
        return predictibilityAnalysisService.createPredictibilityForm2(projectId);
    }*/

    @GetMapping("/predictibility/{analyseId}/score")
    public ResponseEntity<PredictibilityScoreModel> getPredictabilityAnalysisScore(@PathVariable Long analyseId) throws ProjectNotFoundException {
        return predictibilityAnalysisService.calculatePredictibilityAnalysisScore(analyseId);
    }

    @PutMapping("predictibility/isFinished/{analyseId}")
    public ResponseEntity<String> putIsFinishedPredictibilityAnalysis(@PathVariable Long analyseId) throws UserNotAuthorizedException, PredictibilityAnaliseNotFoundException {
        return predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(analyseId);
    }

    @ApiOperation(value = "Increase the iteration number of the predictibility analyse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully"),
            @ApiResponse(responseCode = "403", description = " PredictibilityAnaliseNotFoundException, UserNotAuthorizedException")
    })
    @PutMapping("predictibility/iteration/{analyseId}")
    public ResponseEntity<String> increasePredictibilityAnalyseIterationNumber(@ApiParam(value="Analyse Id.") @PathVariable Long analyseId ) throws UserNotAuthorizedException, PredictibilityAnaliseNotFoundException {
        return predictibilityAnalysisService.increasePredictAnalysisIterationNum(analyseId);
    }
}
