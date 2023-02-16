package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@DynamicInsert
public class User {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
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

    @OneToMany(
            mappedBy = "user"
    )
    @JsonIgnore
    @JsonManagedReference("user_permissions")
    List<ProjectUserPermission> userProjectPermissions;

    @OneToMany(
            mappedBy = "user"
    )
    private List<VerificationToken> verificationTokens;

    @OneToMany(
            mappedBy = "user"
    )
    @JsonManagedReference("user_brainstorming")
    private List<Brainstorming> brainstormings;

    public void detachPermission(ProjectUserPermission projectUserPermission) {
        this.userProjectPermissions.remove(projectUserPermission);
        projectUserPermission.setUser(null);
    }

    @Override
    public String toString() {
        return "User{"
                + "userId=" + userId
                + ", username='" + username + '\''
                + ", userLastName='" + userLastName + '\''
                + ", userFirstName='" + userFirstName + '\''
                + ", userEmail='" + userEmail + '\''
                + ", userAvatar='" + userAvatarPath + '\''
                + '}';
    }
}
