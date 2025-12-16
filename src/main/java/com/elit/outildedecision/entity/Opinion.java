package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "opinion")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Opinion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long opinionId;

  private Integer rating;

  private String description;

  @OneToOne
  @JoinColumn(name = "userId", referencedColumnName = "userId")
  private User user;

  @CreationTimestamp
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Paris")
  private Date createdAt;

  /**
   * get description.
   * @return description
   */
  public String getDescription() {
    return description;
  }

  /**
   * set description.
   * @param description description
   */
  public void setDescription(final String description) {
    this.description = description;
  }
}
