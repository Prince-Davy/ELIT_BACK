
package com.elit.outildedecision.shared.user.controller;

import com.elit.outildedecision.shared.user.exception.AvatarNotFoundException;
import com.elit.outildedecision.shared.user.exception.CouldNotSaveUserException;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import com.elit.outildedecision.shared.user.dto.UserResponse;
import com.elit.outildedecision.shared.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * class used to root API calls for user management. Contains methods to call to
 * display, create, delete or modify users.
 */
@RestController
@RequestMapping("/")
@Tag(name = "Shared - Users", description = "Gestion des utilisateurs")
public class UserController {
  /**
   * UserService to inject.
   */
  private final UserService userService;

  /**
   * Constructor of UserController.
   * dependency injection of UserService to manage users.
   *
   * @param userService UserService
   */
  public UserController(final UserService userService) {
    this.userService = userService;
  }

  /**
   * Method to call to search users by keyword.
   *
   * @param keyword keyword to search
   * @param onlyActive  when true, only active and email verified users are returned
   * @return List of UserResponse that match the keyword
   * @throws IOException IOException
   */
  @GetMapping("/user/users")
  public List<UserResponse> getUsersByKeyword(
      @RequestParam(value = "keyword", required = false) final String keyword,
      @RequestParam(value = "onlyActive", required = false, defaultValue = "false") final boolean onlyActive)
      throws IOException {
    return userService.findUsersByKeyword(keyword, onlyActive);
  }

  /**
   * Method to call to get user's information by id.
   *
   * @param userId id of the user
   * @return UserResponse that contains the user's information
   * @throws IOException IOException
   */
  @GetMapping("/user/{userId}")
  // @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
  public UserResponse getUserById(@PathVariable final Long userId) throws IOException {
    return userService.getAllInfosUserById(userId);
  }

  /**
   * Method to call to update user's password.
   *
   * @param userId  id of the user
   * @param oldPass old password of the user
   * @param newPass new password of the user
   * @return UserResponse that contains the user's information
   * @throws IOException               IOException
   * @throws CouldNotSaveUserException when the user could not be saved in
   *                                   database
   * @throws UserNotFoundException     when the user could not be found in
   *                                   database
   */
  @PutMapping(value = "/user/password/{userId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
  public ResponseEntity<String> updatePasswordUserById(
      @PathVariable final Long userId, @RequestParam final String oldPass, @RequestParam final String newPass)
      throws CouldNotSaveUserException, UserNotFoundException {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserDetails user = (UserDetails) auth.getPrincipal();
    return ResponseEntity.ok(userService.updatePasswordUserById(userId, oldPass, newPass, user.getUsername()));
  }

  /**
   * Method to call to update user's avatar.
   *
   * @param userId     id of the user
   * @param userAvatar the new avatar of the user
   * @return UserResponse that contains the user's information
   * @throws CouldNotSaveUserException when the user could not be saved in
   *                                   database
   * @throws UserNotFoundException     when the user could not be found in
   *                                   database
   */
  @PutMapping(value = "/user/avatar/{userId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  public ResponseEntity<String> uploadAvatarByIdUser(
      @PathVariable final Long userId, @RequestParam final MultipartFile userAvatar)
      throws CouldNotSaveUserException, UserNotFoundException {
    return ResponseEntity.ok(userService.updateAvatarByIdUser(userId, userAvatar));
  }

  /**
   * Method to call to update user's information by id.
   *
   * @param userId        id of the user
   * @param userFirstName the new first name of the user
   * @param userLastName  the new last name of the user
   * @return UserResponse that contains the user's information
   * @throws IOException               IOException
   * @throws CouldNotSaveUserException when the user could not be saved in
   *                                   database
   * @throws UserNotFoundException     when the user could not be found in
   *                                   database
   */
  @PutMapping(value = "/user/info/{userId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  public ResponseEntity<String> updateInfosByIdUser(
      @PathVariable final Long userId,
      @RequestParam final String userFirstName,
      @RequestParam final String userLastName)
      throws IOException, CouldNotSaveUserException, UserNotFoundException {
    return ResponseEntity.ok(userService.updateInfosByIdUser(userId, userFirstName, userLastName));
  }

  /**
   * Method to call to get user's avatar by id.
   *
   * @param userId   id of the user
   * @param response HttpServletResponse to send the avatar to the client
   * @throws IOException             IOException
   * @throws AvatarNotFoundException AvatarNotFoundException
   */
  @GetMapping("/user/avatar/{userId}")
  public void viewAvatarByUserId(final HttpServletResponse response, @PathVariable final Long userId)
      throws IOException, AvatarNotFoundException {
    userService.viewAvatarByUserId(userId, response);
  }

  /**
   * Deletes the avatar for the user with the specified ID.
   *
   * @param userId the ID of the user to update the avatar for
   * @return a ResponseEntity indicating the success or failure of the avatar
   *         update operation
   * @throws IOException             if an I/O error occurs while updating the
   *                                 user account data
   * @throws UserNotFoundException   if the specified user account does not exist
   * @throws AvatarNotFoundException if the specified avatar does not exist
   */
  @DeleteMapping("/user/avatar/{userId}")
  public ResponseEntity<String> deleteAvatarByIdUser(
      @PathVariable final Long userId) throws UserNotFoundException, AvatarNotFoundException {
    return ResponseEntity.ok(userService.deleteAvatarByIdUser(userId));
  }
}
