package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "user")
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
  @OneToMany(mappedBy = "user")
  @JsonIgnore
  @JsonManagedReference("user_permissions")
  private List<ProjectUserPermission> userProjectPermissions;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  private String username;
  private String userLastName;
  private String userFirstName;
  private String userEmail;
  private String userJob;
  private String userAvatarPath;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String userPassword;

  private Boolean isEmailVerified;
  private Boolean isUserActive;
  private Boolean isAdmin;

  @OneToMany(mappedBy = "user")
  private List<VerificationToken> verificationTokens;

  @OneToMany(mappedBy = "user")
  @JsonManagedReference("user_brainstorming")
  private List<Brainstorming> brainstormings;

  /**
   * @param projectUserPermission the projectUserPermission to detach.
   */
  public void detachPermission(final ProjectUserPermission projectUserPermission) {
    this.userProjectPermissions.remove(projectUserPermission);
    projectUserPermission.setUser(null);
  }
  /**
   * @return a string representation of the user.
   */
  @Override
  public String toString() {
    return "User{"
        + "userId="
        + userId
        + ", username='"
        + username
        + '\''
        + ", userLastName='"
        + userLastName
        + '\''
        + ", userFirstName='"
        + userFirstName
        + '\''
        + ", userEmail='"
        + userEmail
        + '\''
        + ", userAvatar='"
        + userAvatarPath
        + '\''
        + '}';
  }
}
