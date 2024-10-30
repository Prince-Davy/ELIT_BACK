package com.elit.outildedecision.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerIaKey implements Serializable {

  private String question;
  private String category;
  private String answer;
  private Long project;
}
