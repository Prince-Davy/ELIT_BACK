package com.elit.outildedecision.shared.kernel.entity;

import com.elit.outildedecision.collaboration.brainstorming.entity.Brainstorming;
import com.elit.outildedecision.shared.auth.entity.VerificationToken;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.List;

/** User class. */
@Entity
@Table(name = "user")
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {
  /** The user project permissions. */
  @OneToMany(mappedBy = "user")
  @JsonIgnore
  @JsonManagedReference("user_permissions")
  private List<ProjectUserPermission> userProjectPermissions;

  /** The user id. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  /** The username. */
  private String username;
  /** The user last name. */
  private String userLastName;
  /** The user first name. */
  private String userFirstName;
  /** The user email. */
  private String userEmail;
  /** The user avatar path. */
  private String userAvatarPath;

  /** The user password. */
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String userPassword;

  /** Whether the email verified flag is set. */
  private Boolean isEmailVerified;
  /** Whether the user active flag is set. */
  private Boolean isUserActive;
  /** Whether the admin flag is set. */
  private Boolean isAdmin;

  /** The verification tokens. */
  @OneToMany(mappedBy = "user")
  private List<VerificationToken> verificationTokens;

  /** The brainstormings. */
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
