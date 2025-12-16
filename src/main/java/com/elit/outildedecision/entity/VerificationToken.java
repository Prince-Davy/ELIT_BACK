package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
  @JsonIgnore
  private User user;

  /**
   * get token.
   * @return token
   */
  public String getToken() {
    return token;
  }

  /**
   * set token.
   * @param token token
   */
  public void setToken(final String token) {
    this.token = token;
  }
}
