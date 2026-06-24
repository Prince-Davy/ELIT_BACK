package com.elit.outildedecision.ai.result.dto;

import com.elit.outildedecision.shared.util.MethodologyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class for recommendations given in json file depending on AI survey (step 1) answers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationModel {

  private MethodologyType id;
  private String title;
  private String description;
  private RecommendationLevel level;

}
