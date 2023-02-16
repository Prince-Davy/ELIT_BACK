package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteModelRequest {
    private Long voteId;
    private String chosenPMM;
    private String justification;
    private Integer predictibilityValue;
    @NotNull(message = "Vous devez renseigner l'id de l'analyse de prévisibilité.")
    @Min(1)
    private Long predictibilityAnalysisId;
    private Boolean isConfirmed;
    private Long userId;

    public VoteModelRequest(Long voteId, String chosenPMM, String justification, Integer predictibilityValue, Long predictibilityAnalysisId, Boolean isConfirmed) {
    }
}
