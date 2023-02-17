package com.elit.outildedecision.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPropertyKey implements Serializable {
  @Column(name = "project_id")
  Long projectId;

  @Column(name = "user_id")
  Long userId;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Message {
    private String username;
    private String avatar;
    private String message;
    private String status;
  }
}
