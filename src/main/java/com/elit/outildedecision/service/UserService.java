
package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.CouldNotSaveUserException;
import com.elit.outildedecision.exception.UserAlreadyExistsException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.UserModel;
import com.elit.outildedecision.model.UserResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
   * Saves a new user account.
   *
   * @param user a UserModel object representing the user account to save
   * @return a ResponseEntity indicating the success or failure of the save operation
   * @throws CouldNotSaveUserException  if the specified user account could not be saved
   * @throws UserAlreadyExistsException if the specified user account already exists
   */
  ResponseEntity<String> saveUser(UserModel user) throws CouldNotSaveUserException, UserAlreadyExistsException;

  /**
   * Updates an existing user account.
   *
   * @param user a UserModel object representing the updated user account
   * @return a ResponseEntity indicating the success or failure of the update operation
   * @throws CouldNotSaveUserException if the specified user account could not be updated
   */
  ResponseEntity<Map<String, String>> updateUser(UserModel user) throws CouldNotSaveUserException;

  /**
   * Updates the password for the user with the specified ID.
   *
   * @param userId  the ID of the user to update the password for
   * @param oldPass a String representing the user's current password
   * @param newPass a String representing the user's new password
   * @return a ResponseEntity indicating the success or failure of the password update operation
   * @throws IOException               if an I/O error occurs while updating the user account data
   * @throws CouldNotSaveUserException if the specified user account could not be updated
   */
  ResponseEntity<String> updatePasswordUserById(Long userId, String oldPass, String newPass)
    throws IOException, CouldNotSaveUserException;

  /**
   * Updates the avatar for the user with the specified ID.
   *
   * @param userId     the ID of the user to update the avatar for
   * @param userAvatar a MultipartFile object representing the user's new avatar
   * @return a ResponseEntity indicating the success or failure of the avatar update operation
   * @throws IOException               if an I/O error occurs while updating the user account data
   * @throws CouldNotSaveUserException if the specified user account could not be updated
   * @throws UserNotFoundException     if the specified user account does not exist
   */
  ResponseEntity<String> updateAvatarByIdUser(Long userId, MultipartFile userAvatar)
    throws IOException, CouldNotSaveUserException, UserNotFoundException;

  /**
   * Updates the information (first name, last name) for the user with the specified ID.
   *
   * @param userId        the ID of the user to update the information for
   * @param userFirstName a String representing the user's new first name
   * @param userLastName  a String representing the user's new last name
   * @return a ResponseEntity indicating the success or failure of the information update operation
   * @throws IOException               if an I/O error occurs while updating the user account data
   * @throws CouldNotSaveUserException if the specified user account could not be updated
   */
  ResponseEntity<String> updateInfosByIdUser(Long userId, String userFirstName, String userLastName)
    throws IOException, CouldNotSaveUserException;

  /**
   * Retrieves the avatar for the user with the specified ID.
   *
   * @param userId   the ID of the user
   *                 to retrieve the avatar for
   * @param response the HttpServletResponse object to write the avatar to
   * @throws IOException if an I/O error occurs while retrieving the avatar
   */
  void viewAvatarByUserId(Long userId, HttpServletResponse response) throws IOException;

  /**
   * Sends a registration confirmation email to the specified user.
   *
   * @param user a User object representing the user to send the registration confirmation email to
   */
  void sendRegistrationConfirmationEmail(User user);

  /**
   * Searches for users matching the specified keyword.
   *
   * @param keyword a String representing the keyword to search for
   * @return a List of UserResponseModel objects representing the users that match the specified keyword
   * @throws IOException if an I/O error occurs while retrieving the user data
   */
  List<UserResponseModel> findUsersByKeyword(String keyword) throws IOException;

  /**
   * Retrieves all information about the user with the specified ID.
   *
   * @param userId the ID of the user to retrieve information for
   * @return a UserResponseModel object representing the user's information
   * @throws IOException if an I/O error occurs while retrieving the user data
   */
  UserResponseModel getAllInfosUserById(Long userId) throws IOException;

  /**
   * Saves the avatar for the specified user.
   *
   * @param avatar a MultipartFile object representing the user's avatar to save
   * @return a String representing the file name of the saved avatar
   * @throws UserNotFoundException if the specified user account does not exist
   * @throws IOException           if an I/O error occurs while saving the avatar
   */
  String saveAvatar(MultipartFile avatar) throws UserNotFoundException, IOException;
}
