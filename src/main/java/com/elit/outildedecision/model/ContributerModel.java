package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContributerModel implements Serializable {
  private Long contributerId;
  private String firstName;
  private String lastName;
  private String email;
  private String job;
  private Boolean isAdmin;
  private String role;
}
