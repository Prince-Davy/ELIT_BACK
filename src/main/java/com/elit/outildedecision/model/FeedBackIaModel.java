package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackIaModel {
  private static final long serialVersionUID = 1L;

  private Boolean isLiked;

  private String methodology;

  private String feedbackAnswer;

  private Long projectId;

  private Long userId;

}
