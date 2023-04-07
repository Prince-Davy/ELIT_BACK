package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
}
