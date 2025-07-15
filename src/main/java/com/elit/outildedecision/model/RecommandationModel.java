package com.elit.outildedecision.model;

import com.elit.outildedecision.enumeration.MethodologyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class for recommandations given in json file depending on AI survey (step 1) answers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommandationModel {

  private MethodologyType id;
  private String title;
  private String description;
  private RecommandationLevel level;

}
