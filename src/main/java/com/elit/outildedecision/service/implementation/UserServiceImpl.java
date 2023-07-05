
package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.entity.VerificationToken;
import com.elit.outildedecision.exception.AvatarNotFoundException;
import com.elit.outildedecision.exception.CouldNotSaveUserException;
import com.elit.outildedecision.exception.UserAlreadyExistsException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.UserModel;
import com.elit.outildedecision.model.UserResponseModel;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.security.AppUserPrincipal;
import com.elit.outildedecision.service.UserService;
import com.elit.outildedecision.service.VerificationTokenService;
import com.elit.outildedecision.util.Constants;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Service implementation for managing user-related operations.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

  /**
   * UserRepository instance to interact with the database.
   */
  private final UserRepository userRepository;

  /**
   * PasswordEncoder instance to encode and decode passwords.
   */
  private final PasswordEncoder passwordEncoder;

  /**
   * JavaMailSender instance to send emails.
   */
  private final JavaMailSender javaMailSender;

  /**
   * VerificationTokenService instance to manage verification tokens.
   */
  private final VerificationTokenService verificationTokenService;
  /**
   * Logger instance for logging.
   */
  private final Logger logger;
  /**
   * Directory to store user avatar files.
   */
  @Value("${avatar.upload-dir}")
  private String avatarUploadDir;
  /**
   * Email address to send emails from.
   */
  @Value("${spring.mail.username}")
  private String senderEmail;
  /**
   * Value of backend url.
   */
  @Value("${backend.value-url}")
  private String backEndUrl;
  /**
   * Constructor for UserServiceImpl.
   *
   * @param userRepository           UserRepository instance to interact with the database.
   * @param passwordEncoder          PasswordEncoder instance to encode and decode passwords.
   * @param javaMailSender           JavaMailSender instance to send emails.
   * @param verificationTokenService VerificationTokenService instance to manage verification tokens.
   */
  public UserServiceImpl(
    final UserRepository userRepository,
    final PasswordEncoder passwordEncoder,
    final JavaMailSender javaMailSender,
    final VerificationTokenService verificationTokenService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.javaMailSender = javaMailSender;
    this.verificationTokenService = verificationTokenService;
    this.logger = LoggerFactory.getLogger(UserServiceImpl.class);
  }

  /**
   * Method to load user by username from the database.
   *
   * @param username Username of the user to load.
   * @return UserDetails object representing the user.
   * @throws UsernameNotFoundException if user is not found in the database.
   */
  @SneakyThrows
  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    User user = userRepository.findByUserEmailAndIsVerified(username);
    return new AppUserPrincipal(user);
  }

  /**
   * Method to find user by email address.
   *
   * @param email Email address of the user to find.
   * @return User object representing the user.
   */
  @Override
  public User findByEmail(final String email) {
    return userRepository.findByUserEmail(email);
  }

  /**
   * Method to save new user to the database.
   *
   * @param user UserModel object containing user information.
   * @return ResponseEntity object with HTTP status code and message.
   * @throws UserAlreadyExistsException if user already exists in the database.
   */
  @Override
  public ResponseEntity<String> saveUser(final UserModel user) throws UserAlreadyExistsException, CouldNotSaveUserException {
    if (!user.getUserPassword().equals(user.getUserPasswordConfirmation())) {
      throw new CouldNotSaveUserException("Les mots de passes ne sont pas identiques.");
    }
    if (userRepository.existsByUserEmail(user.getUserEmail())) {
      throw new UserAlreadyExistsException("Une erreur est survenue, le compte associé à cet e-mail existe déjà");
    }
    try {
      User userToSave =
        User.builder()
          .userEmail(user.getUserEmail())
          .isUserActive(false)
          .isEmailVerified(false)
          .username(user.getUsername())
          .userFirstName(user.getUserFirstName())
          .userLastName(user.getUserLastName())
          .userPassword(passwordEncoder.encode(user.getUserPassword()))
          .isAdmin(false)
          .build();
      sendRegistrationConfirmationEmail(userRepository.save(userToSave));
      return ResponseEntity.status(HttpStatus.CREATED).body("Votre compte a été créé avec succès");
    } catch (Exception e) {

      throw new CouldNotSaveUserException("Une erreur est survenue lors de votre inscription. Veuillez réessayer plus tard");
    }
  }
  /**
   * Updates a user's password given their ID and old and new passwords.
   *
   * @param userId  The ID of the user to update.
   * @param oldPass The user's current password.
   * @param newPass The user's new password.
   * @return A ResponseEntity with a status code and message indicating the outcome of the update.
   */
  @Override
  public ResponseEntity<String> updatePasswordUserById(final Long userId,
                                                       final String oldPass,
                                                       final String newPass) throws UserNotFoundException {
    User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(Constants.USER_NOT_FOUND));
    String currentPass = user.getUserPassword();
    boolean checkPassword = passwordEncoder.matches(oldPass, currentPass);
    if (!checkPassword) {
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
        .body("Votre ancien mot de passe n'est pas correct");
    } else {
      user.setUserPassword(passwordEncoder.encode(newPass));
    }
    userRepository.save(user);
    return ResponseEntity.status(HttpStatus.OK).body("Votre mot de passe a bien été modifié");
  }

  /**
   * Updates a user's avatar given their ID and a new avatar file.
   *
   * @param userId     The ID of the user to update.
   * @param userAvatar The new avatar file.
   * @return A ResponseEntity with a status code and message indicating the outcome of the update.
   * @throws UserNotFoundException If the specified user cannot be found.
   */
  @Override
  public ResponseEntity<String> updateAvatarByIdUser(final Long userId,
                                                     final MultipartFile userAvatar)
    throws UserNotFoundException, CouldNotSaveUserException {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new UserNotFoundException(Constants.USER_NOT_FOUND));
    try {
        String avatarPath = saveAvatar(userAvatar);
        user.setUserAvatarPath(avatarPath);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body("Votre photo a bien été modifiée.");
  } catch (Exception e) {

        throw new CouldNotSaveUserException("Une erreur est survenue lors de l'enregistrement de votre photo.");
    }
  }

  /**
   * Updates a user's first name and/or last name given their ID.
   *
   * @param userId        The ID of the user to update.
   * @param userFirstName The user's new first name (may be null).
   * @param userLastName  The user's new last name (may be null).
   * @return A ResponseEntity with a status code and message indicating the outcome of the update.
   * @throws CouldNotSaveUserException If there is an error saving the updated user to the database.
   * @throws UserNotFoundException     If the specified user cannot be found.
   */
  @Override
  @Transactional
  public ResponseEntity<String> updateInfosByIdUser(final Long userId,
                                                    final String userFirstName,
                                                    final String userLastName) throws CouldNotSaveUserException, UserNotFoundException {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new UserNotFoundException(Constants.USER_NOT_FOUND));
    user.setUserFirstName(userFirstName);
    user.setUserLastName(userLastName);
    try {
      userRepository.save(user);
    } catch (Exception e) {
      throw new CouldNotSaveUserException("Erreur lors de l'enregistrement");
    }

    return ResponseEntity.status(HttpStatus.OK).body("Vos modifications ont bien été enregistrées");
  }


  /**
   * Sends a user's avatar file to the client in the response body.
   *
   * @param userId   The ID of the user whose avatar should be sent.
   * @param response The response to write the avatar file to.
   * @return A ResponseEntity with a message avatar not found
   * @throws IOException             If there is an error reading or writing the avatar file.
   */
  @Override
  public ResponseEntity<String> viewAvatarByUserId(final Long userId,
                                                   final HttpServletResponse response) throws IOException {
    User user = userRepository.getById(userId);
    String pathAvatar = user.getUserAvatarPath();
    if (!Objects.isNull(pathAvatar)) {
      File file = new File(pathAvatar);
      String mimeType = URLConnection.guessContentTypeFromName(file.getName());
      if (mimeType == null) {
        mimeType = "application/octet-stream";
      }

      response.setContentType(mimeType);
      response.setHeader(
        "Content-Disposition", String.format("inline; filename=\"%s\"", file.getName()));
      response.setContentLength((int) file.length());

      try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
        FileCopyUtils.copy(inputStream, response.getOutputStream());
      }
    }
      logger.error(Constants.AVATAR_NOT_FOUND);
      return ResponseEntity.status(200).body(Constants.AVATAR_NOT_FOUND);

  }
  /**
   * Updates a user's information given a UserModel object containing the updated information.
   *
   * @param user The UserModel object containing the updated user information.
   * @return A ResponseEntity with a status code and message indicating the outcome of the update.
   * @throws CouldNotSaveUserException If there is an error saving the updated user to the database.
   */
  @Override
  public ResponseEntity<Map<String, String>> updateUser(final UserModel user)
    throws CouldNotSaveUserException {
    String authenticatedUserEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User userFromDB = userRepository.findByUserEmail(authenticatedUserEmail);
    Map<String, String> errorMap = new HashMap<>();
    if (Objects.isNull(userFromDB)) {
      errorMap.put("NOT_FOUND", Constants.USER_NOT_FOUND);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
    }

    errorMap = validateUserUpdate(user, userFromDB);
    if (!errorMap.isEmpty()) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMap);
    }

    updateUserFields(user, userFromDB);
    userRepository.save(userFromDB);

    errorMap.put("SUCCESS", "Utilisateur mis à jour");
    return ResponseEntity.status(HttpStatus.OK).body(errorMap);
  }
  /**
   * Validates a user details against the user details in the database.
   *
   * @param user The UserModel object containing the updated user information.
   * @param userFromDB The user to update.
   * @return A Map containing the errors.
   */
  private Map<String, String> validateUserUpdate(final UserModel user, final User userFromDB) {
    Map<String, String> errorMap = new HashMap<>();

    if (userRepository.findByUserEmail(user.getUserEmail()) != null && !userFromDB.getUserEmail().equals(user.getUserEmail())) {
      errorMap.put(Constants.CONFLICT, "email déjà utilisé");
    }
    if (!user.getUserPassword().equals(user.getUserPasswordConfirmation())) {
      errorMap.put(Constants.CONFLICT, "Les mots de passe doivent être identiques");
    }
    if (!user.getUserEmail().equals(user.getUserEmailConfirmation())) {
      errorMap.put(Constants.CONFLICT, "Les emails doivent être identiques");
    }
    if (userRepository.findByUsername(user.getUsername()) != null
      && !userFromDB.getUsername().equals(user.getUsername())) {
      errorMap.put(Constants.CONFLICT, "nom d'utilisateur déjà utilisé");
    }
    return errorMap;
  }
  /**
   * Updates a user's fields.
   *
   * @param user The UserModel object containing the updated user information.
   * @param userFromDB The user to update.
   * @throws CouldNotSaveUserException If there is an error saving the updated user to the database.
   */
  private void updateUserFields(final UserModel user, final User userFromDB) throws CouldNotSaveUserException {
    if (user.getUsername() != null) {
      userFromDB.setUsername(user.getUsername());
    }
    if (!Objects.isNull(user.getUserPassword())) {
      userFromDB.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
    }
    if (!Objects.isNull(user.getUserFirstName())) {
      userFromDB.setUserFirstName(user.getUserFirstName());
    }
    if (!Objects.isNull(user.getUserLastName())) {
      userFromDB.setUserLastName(user.getUserLastName());
    }
    if (!Objects.isNull(user.getUserEmail())) {
      userFromDB.setUserEmail(user.getUserEmail());
    }
    if (!Objects.isNull(user.getUserAvatar())) {
      try {
        userFromDB.setUserAvatarPath(saveAvatar(user.getUserAvatar()));
      } catch (Exception e) {
        throw new CouldNotSaveUserException("Erreur lors de l'enregistrement de l'avatar");
      }
    }
  }


  /**
   * Sends a registration confirmation email to a user containing a verification token.
   *
   * @param user The user to send the email to.
   */
  @Override
  public void sendRegistrationConfirmationEmail(final User user) {
    VerificationToken verificationToken =
      verificationTokenService.createToken(user, "confirm-account");
    String confirmationUrl =
      backEndUrl + "/confirm-account?token=" + verificationToken.getToken();
    String mailBody =
      "Bonjour "
        + " "
        + user.getUserFirstName()
        + " "
        + user.getUserLastName()
        + ",\n\n"
        + "Veuillez cliquer sur le lien ci-dessous pour confirmer votre compte :\n"
        + confirmationUrl
        + "\n\n"
        + "Cordialement,\n"
        + "L'équipe Outil de décision ELIT";
    String mailSubject = "ELIT : Confirmation de votre compte";
    try {
      SimpleMailMessage mailMessage = new SimpleMailMessage();
      mailMessage.setFrom(senderEmail);
      mailMessage.setTo(user.getUserEmail());
      mailMessage.setText(mailBody);
      mailMessage.setSubject(mailSubject);
      javaMailSender.send(mailMessage);
    } catch (Exception e) {
      logger.error("Erreur lors de l'envoi du mail de confirmation d'inscription");
    }
  }

  /**
   * Gets all available information about a user given their ID.
   *
   * @param userId The ID of the user to get information about.
   * @return A UserResponseModel object containing the requested information.
   */
  @Override
  public UserResponseModel getAllInfosUserById(final Long userId) {
    User user = userRepository.getById(userId);
      return UserResponseModel.builder()
        .userId(user.getUserId())
        .userFirstName(user.getUserFirstName())
        .userLastName(user.getUserLastName())
        .username(user.getUsername())
        .userEmail(user.getUserEmail())
        .build();
  }

  /**
   * Finds all users whose email or username contains a given keyword.
   *
   * @param keyword The keyword to search for.
   * @return A list of UserResponseModel objects containing information about the found users.
   * @throws IOException If there is an error reading a user's avatar file.
   */
  @Override
  public List<UserResponseModel> findUsersByKeyword(final String keyword) throws IOException {
    List<UserResponseModel> foundUsers = new ArrayList<>();
    if (keyword == null || "".equals(keyword)) {
      for (User user : userRepository.findAll()) {
        if (user.getUserAvatarPath() != null) {
          File file = new File(user.getUserAvatarPath());
          UserResponseModel userResponseModel =
            UserResponseModel.builder()
              .userId(user.getUserId())
              .userFirstName(user.getUserFirstName())
              .userLastName(user.getUserLastName())
              .username(user.getUsername())
              .userEmail(user.getUserEmail())
              .userAvatar(Files.readAllBytes(file.toPath()))
              .build();
          foundUsers.add(userResponseModel);

        } else {
          UserResponseModel userResponseModel =
            UserResponseModel.builder()
              .userId(user.getUserId())
              .userFirstName(user.getUserFirstName())
              .userLastName(user.getUserLastName())
              .username(user.getUsername())
              .userEmail(user.getUserEmail())
              .build();
          foundUsers.add(userResponseModel);
        }
      }
      return foundUsers;
    }
    for (User user : userRepository.findAllByUserEmailOrUsernameContaining(keyword)) {
      UserResponseModel userResponseModel =
        UserResponseModel.builder()
          .userFirstName(user.getUserFirstName())
          .userLastName(user.getUserLastName())
          .username(user.getUsername())
          .userEmail(user.getUserEmail())
          .userAvatar(Files.readAllBytes(Paths.get(user.getUserAvatarPath())))
          .build();
      foundUsers.add(userResponseModel);
    }
    return foundUsers;
  }

  /**
   * Saves the avatar file uploaded by the user to the specified directory.
   * @param avatar the avatar file uploaded by the user
   * @return the file path where the avatar file was saved
   * @throws IOException if an I/O error occurs while saving the file
   */
  @Override
  public String saveAvatar(final MultipartFile avatar) throws IOException {
    String filePath;
    String folderPath = StringUtils.cleanPath(avatarUploadDir);
    filePath = StringUtils.cleanPath(folderPath + avatar.getOriginalFilename());
    Files.copy(avatar.getInputStream(), Path.of(filePath), StandardCopyOption.REPLACE_EXISTING);
    return filePath;
  }

  /**
   * Deletes the avatar for the user with the specified ID.
   * @param userId     the ID of the user to update the avatar for
   * @return a ResponseEntity indicating the success or failure of the avatar update operation
   * @throws UserNotFoundException     if the specified user account does not exist
   * @throws AvatarNotFoundException     if the specified avatar  does not exist
   */
  @Override
  public ResponseEntity<String> deleteAvatarByIdUser(final Long userId) throws AvatarNotFoundException, UserNotFoundException {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new UserNotFoundException(Constants.USER_NOT_FOUND));

    String pathAvatar = user.getUserAvatarPath();
    if (Objects.isNull(pathAvatar)) {
      throw new AvatarNotFoundException(Constants.AVATAR_NOT_FOUND);
    }

    File avatarToDelete = new File(pathAvatar);
    if (FileSystemUtils.deleteRecursively(avatarToDelete)) {
      user.setUserAvatarPath(null);
      return ResponseEntity.ok().body("Photo de profil supprimé avec succès");
    } else {
      return ResponseEntity.badRequest().body("Erreur lors de la suppression de la photo de profil.");
    }
  }

}
