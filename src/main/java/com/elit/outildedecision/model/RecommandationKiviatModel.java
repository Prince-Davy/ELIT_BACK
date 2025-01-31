package com.elit.outildedecision.model;

import com.elit.outildedecision.util.MethodologyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class for recommandations given in json file depending on AI survey (step 1) answers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommandationKiviatModel {

  private MethodologyType id;
  private double FlexibiliteGestion;
  private double FlexibiliteSolution;
  private double ConfianceEtDelegation;
  private double MaitrisePerimetre;
  private double Coordination;


}
