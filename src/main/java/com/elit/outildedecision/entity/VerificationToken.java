package com.elit.outildedecision.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "verification_token")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerificationToken implements Serializable {
  @Id
  @Column(name = "token")
  private String token;

  private Date expiryDate;

  private String type;

  @ManyToOne(fetch = FetchType.EAGER)
  private User user;
}
