package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notification")
public class Notification implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long notificationId;

  private String title;
  private String body;
  private String icon;

  @CreationTimestamp
  @JsonFormat(pattern = "yyyy-MM-dd HH-mm", timezone = "Europe/Paris")
  private Date notificationDate;

  @ManyToOne
  @JoinColumn(name = "project_id", referencedColumnName = "projectId")
  @JsonBackReference("notification_project")
  private Project project;

  /**
   * get title.
   * @return title
   */
  public String getTitle() {
    return title;
  }

  /**
   * set title.
   * @param title title
   */
  public void setTitle(final String title) {
    this.title = title;
  }
}
