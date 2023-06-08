
package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.CouldNotSaveUserException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.UserModel;
import com.elit.outildedecision.model.UserResponseModel;
import com.elit.outildedecision.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * class used to root API calls for user management. Contains methods to call to display, create,
 * delete or modify users.
 */
@RestController
@RequestMapping("/")
public class UserController {
  /**
   * UserService to inject.
   */
  private final UserService userService;
  /**
   * Constructor of UserController.
   * dependency injection of UserService to manage users.
   * @param userService UserService
   */
  public UserController(final UserService userService) {
    this.userService = userService;
  }
  /**
   * Method to call to search users by keyword.
   *
   * @param keyword keyword to search
   * @return List of UserResponseModel that match the keyword
   * @throws IOException IOException
   */
  @GetMapping("/user/users")
  public List<UserResponseModel> getUsersByKeyword(
      @RequestParam(value = "keyword", required = false) final String keyword) throws IOException {
    return userService.findUsersByKeyword(keyword);
  }
  /**
   * Method to call to update user's information.
   *
   * @param userModel UserModel that contains the new information
   * @return List of UserResponseModel that match the keyword
   * @throws CouldNotSaveUserException when the user could not be saved in database
   */
  @PutMapping(
      value = "/user/update",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Map<String, String>> updateUser(@Valid @ModelAttribute final UserModel userModel)
      throws CouldNotSaveUserException {
    return userService.updateUser(userModel);
  }
  /**
   * Method to call to get user's information by id.
   *
   * @param userId id of the user
   * @return UserResponseModel that contains the user's information
   * @throws IOException IOException
   */
  @GetMapping("/user/{userId}")
  @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
  public UserResponseModel getUserById(@PathVariable final Long userId) throws IOException {
    return userService.getAllInfosUserById(userId);
  }
  /**
   * Method to call to update user's password.
   *
   * @param userId id of the user
   * @param oldPass old password of the user
   * @param newPass new password of the user
   * @return UserResponseModel that contains the user's information
   * @throws IOException IOException
   * @throws CouldNotSaveUserException when the user could not be saved in database
   * @throws UserNotFoundException when the user could not be found in database
   */
  @PutMapping(
      value = "/user/password/{userId}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
  public ResponseEntity<String> updatePasswordUserById(
      @PathVariable final Long userId, @RequestParam final String oldPass, @RequestParam final String newPass)
    throws IOException, CouldNotSaveUserException, UserNotFoundException {
    return userService.updatePasswordUserById(userId, oldPass, newPass);
  }
  /**
   * Method to call to update user's avatar.
   *
   * @param userId id of the user
   * @param userAvatar the new avatar of the user
   * @return UserResponseModel that contains the user's information
   * @throws IOException IOException
   * @throws CouldNotSaveUserException when the user could not be saved in database
   * @throws UserNotFoundException when the user could not be found in database
   */
  @PutMapping(
      value = "/user/avatar/{userId}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<String> updateAvatarByIdUser(
      @PathVariable final Long userId, @RequestParam final MultipartFile userAvatar)
    throws IOException, CouldNotSaveUserException, UserNotFoundException {
    return userService.updateAvatarByIdUser(userId, userAvatar);
  }
  /**
   * Method to call to update user's information by id.
   *
   * @param userId id of the user
   * @param userFirstName the new first name of the user
   * @param userLastName the new last name of the user
   * @return UserResponseModel that contains the user's information
   * @throws IOException IOException
   * @throws CouldNotSaveUserException when the user could not be saved in database
   * @throws UserNotFoundException when the user could not be found in database
   */
  @PutMapping(
      value = "/user/info/{userId}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<String> updateInfosByIdUser(
      @PathVariable final Long userId,
      @RequestParam final String userFirstName,
      @RequestParam final String userLastName)
    throws IOException, CouldNotSaveUserException, UserNotFoundException {
    return userService.updateInfosByIdUser(userId, userFirstName, userLastName);
  }
  /**
   * Method to call to get user's avatar by id.
   *
   * @param userId id of the user
   * @param response HttpServletResponse to send the avatar to the client
   * @throws IOException IOException
   */
  @GetMapping("/user/avatar/{userId}")
  public void viewAvatarByUserId(final HttpServletResponse response, @PathVariable final Long userId)
      throws IOException {
    userService.viewAvatarByUserId(userId, response);
  }
}
