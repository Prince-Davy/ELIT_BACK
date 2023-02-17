package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrainstormingModel {
  private Long brainstormingId;
  private String brainstormingText;
  private Boolean isChecked;
  private Long questionId;
  private Long predictibilityAnalysisId;
  private Long userId;
  private String firstName;
  private String lastName;
  private String role;
}
