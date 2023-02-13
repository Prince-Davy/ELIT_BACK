package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PredictibilityResults {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long predictibilityResultsId;
    @ManyToOne()
    @JoinColumn(
            name = "predictibilityAnalysisId",
            referencedColumnName = "predictibility_analysis_id"
    )
    @JsonBackReference("predictibility_analysis_results")
    private PredictibilityAnalysis predictibilityAnalysis;
    private String name;
    private Float value;
}
