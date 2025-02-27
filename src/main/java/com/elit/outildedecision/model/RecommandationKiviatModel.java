package com.elit.outildedecision.model;

import com.elit.outildedecision.util.MethodologyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The RecommandationKiviatModel class is used for make kiviat graph.
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
