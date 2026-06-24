
package com.elit.outildedecision.shared.user.service;

import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.user.exception.AvatarNotFoundException;
import com.elit.outildedecision.shared.exception.CouldNotMailUserException;
import com.elit.outildedecision.shared.user.exception.CouldNotSaveUserException;
import com.elit.outildedecision.shared.user.exception.UserAlreadyExistsException;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import com.elit.outildedecision.shared.auth.dto.UserRequest;
import com.elit.outildedecision.shared.user.dto.UserResponse;
import com.elit.outildedecision.shared.exception.ForbiddenException;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * This interface defines the contract for the User Service, which provides methods to manage user accounts.
 */
public interface UserService {

  /**
   * Retrieves the User object with the specified email address.
   *
   * @param email a String representing the email address of the user to retrieve
   * @return the User object with the specified email address
   */
  User findByEmail(String email);

  /**
   * Retrieves the permission labels held by a user on a given project.
   * <p>
   * Backed by a projection query so it does not navigate the lazy {@code User#userProjectPermissions}
   * graph: usable by the method-security expressions which run outside any transaction
   * ({@code spring.jpa.open-in-view=false}).
   *
   * @param email     the email of the authenticated user
   * @param projectId the id of the project to check permissions for
   * @return the permission labels held by the user on the project (possibly empty)
   */
  List<String> getProjectPermissions(String email, Long projectId);

  /**
   * Retrieves the permission labels held by a user on the project that owns the given audit.
   *
   * @param email   the email of the authenticated user
   * @param auditId the id of the audit whose owning project is checked
   * @return the permission labels held by the user on that project (possibly empty)
   */
  List<String> getProjectPermissionsByAudit(String email, Long auditId);

  /**
   * Saves a new user account.
   *
   * @param user a UserRequest object representing the user account to save
   * @return the success confirmation message
   * @throws CouldNotSaveUserException  if the specified user account could not be saved
   * @throws UserAlreadyExistsException if the specified user account already exists
   */
  String saveUser(UserRequest user) throws CouldNotSaveUserException, UserAlreadyExistsException;

  /**
   * Updates the password for the user with the specified ID.
   *
   * @param userId            the ID of the user to update the password for
   * @param oldPass           a String representing the user's current password
   * @param newPass           a String representing the user's new password
   * @param authenticatedEmail the email of the authenticated user (must own the account)
   * @return the success confirmation message
   * @throws UserNotFoundException if the specified user account does not exist
   * @throws ForbiddenException    if the authenticated user does not own the account
   */
  String updatePasswordUserById(Long userId, String oldPass, String newPass, String authenticatedEmail)
    throws UserNotFoundException, ForbiddenException;

  /**
   * Updates the avatar for the user with the specified ID.
   *
   * @param userId     the ID of the user to update the avatar for
   * @param userAvatar a MultipartFile object representing the user's new avatar
   * @return the success confirmation message
   * @throws CouldNotSaveUserException if the specified user account could not be updated
   * @throws UserNotFoundException     if the specified user account does not exist
   */
  String updateAvatarByIdUser(Long userId, MultipartFile userAvatar)
    throws CouldNotSaveUserException, UserNotFoundException;

  /**
   * Updates the information (first name, last name) for the user with the specified ID.
   *
   * @param userId        the ID of the user to update the information for
   * @param userFirstName a String representing the user's new first name
   * @param userLastName  a String representing the user's new last name
   * @return the success confirmation message
   * @throws CouldNotSaveUserException if the specified user account could not be updated
   * @throws UserNotFoundException     if the specified user account does not exist
   */
  String updateInfosByIdUser(Long userId, String userFirstName, String userLastName)
    throws CouldNotSaveUserException, UserNotFoundException;

  /**
   * Retrieves the avatar for the user with the specified ID.
   *
   * @param userId   the ID of the user
   *                 to retrieve the avatar for
   * @param response the HttpServletResponse object to write the avatar to
   * @throws IOException if an I/O error occurs while retrieving the avatar
   */
  void viewAvatarByUserId(Long userId, HttpServletResponse response) throws IOException, AvatarNotFoundException;

  /**
   * Creates a confirmation token and sends a registration confirmation email to the specified user.
   *
   * @param user a User object representing the user to send the registration confirmation email to
   */
  void sendRegistrationConfirmationEmail(User user);

  /**
   * Searches for users matching the specified keyword.
   *
   * @param keyword a String representing the keyword to search for
   * @return a List of UserResponse objects representing the users that match the specified keyword
   * @throws IOException if an I/O error occurs while retrieving the user data
   */
  List<UserResponse> findUsersByKeyword(String keyword) throws IOException;

  /**
   * Retrieves all information about the user with the specified ID.
   *
   * @param userId the ID of the user to retrieve information for
   * @return a UserResponse object representing the user's information
   * @throws IOException if an I/O error occurs while retrieving the user data
   */
  UserResponse getAllInfosUserById(Long userId) throws IOException;

  /**
   * Saves the avatar for the specified user.
   *
   * @param avatar a MultipartFile object representing the user's avatar to save
   * @return a String representing the file name of the saved avatar
   * @throws UserNotFoundException if the specified user account does not exist
   * @throws IOException           if an I/O error occurs while saving the avatar
   */
  String saveAvatar(MultipartFile avatar) throws UserNotFoundException, IOException;

  /**
   * Deletes the avatar for the user with the specified ID.
   *
   * @param userId     the ID of the user to update the avatar for
   * @return the success confirmation message
   * @throws UserNotFoundException     if the specified user account does not exist
   * @throws AvatarNotFoundException     if the specified avatar  does not exist
   */
  String deleteAvatarByIdUser(Long userId) throws AvatarNotFoundException, UserNotFoundException;

  /**
   * Sends a registration confirmation email to the specified user again.
   *
   * @param user the user to send the registration confirmation email to again
   * @return the success confirmation message
   * @throws UserAlreadyExistsException if the account is already active
   * @throws CouldNotMailUserException  if the confirmation email could not be sent
   */
  String sendRegistrationConfirmationEmailAgain(UserRequest user) throws UserAlreadyExistsException, CouldNotMailUserException;
}
