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
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPropertyKey implements Serializable {

  private static final long serialVersionUID = 1L;
  @Column(name = "project_id")
  private Long projectId;

  @Column(name = "user_id")
  private Long userId;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Message {
    private String username;
    private String avatar;
    private String messageBody;
    private String status;
  }
}
