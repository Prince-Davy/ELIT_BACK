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
import java.awt.print.Pageable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UserService {
  User findByUsername(String username);

  User findByEmail(String email);

  User findByUserId(Long userId);

  ResponseEntity<String> saveUser(UserModel user) throws CouldNotSaveUserException, UserAlreadyExistsException;

  void deleteUserById(Long userId);

  ResponseEntity<Map<String, String>> updateUser(UserModel user) throws CouldNotSaveUserException;

  ResponseEntity<String> updatePasswordUserById(Long userId, String oldPass, String newPass)
      throws IOException, CouldNotSaveUserException;

  ResponseEntity<String> updateAvatarByIdUser(Long userId, MultipartFile userAvatar)
    throws IOException, CouldNotSaveUserException, UserNotFoundException;

  ResponseEntity<String> updateInfosByIdUser(Long userId, String userFirstName, String userLastName)
      throws IOException, CouldNotSaveUserException;

  void viewAvatarByUserId(Long userId, HttpServletResponse response) throws IOException;

  ResponseEntity<Object> uploadAttachment(File file, Long userId) throws UserNotFoundException;

  void sendRegistrationConfirmationEmail(User user);

  List<User> findAllUsers();

  List<User> findAllUsersByPage(Pageable pageable);

  void confirmUser(User user);

  List<UserResponseModel> findUsersByKeyword(String keyword) throws IOException;

  UserResponseModel getAllInfosUserById(Long userId) throws IOException;

  String saveAvatar(MultipartFile avatar) throws UserNotFoundException, IOException;
}
