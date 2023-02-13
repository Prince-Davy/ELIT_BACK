package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseModel {
    private Long userId;

    private String username;

    private String userLastName;

    private String userFirstName;

    private String userEmail;

    private String userJob;

    private byte[] userAvatar;
}
