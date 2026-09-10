package com.elit.outildedecision.shared.user.service.implementation;

import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.auth.entity.VerificationToken;
import com.elit.outildedecision.shared.user.exception.AvatarNotFoundException;
import com.elit.outildedecision.shared.exception.BadRequestException;
import com.elit.outildedecision.shared.exception.ConflictException;
import com.elit.outildedecision.shared.exception.CouldNotMailUserException;
import com.elit.outildedecision.shared.exception.ForbiddenException;
import com.elit.outildedecision.shared.user.exception.CouldNotSaveUserException;
import com.elit.outildedecision.shared.user.exception.UserAlreadyExistsException;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import com.elit.outildedecision.shared.auth.dto.UserRequest;
import com.elit.outildedecision.shared.user.dto.UserResponse;
import com.elit.outildedecision.shared.user.event.UserRegisteredEvent;
import com.elit.outildedecision.shared.user.entity.UserAvatarContent;
import com.elit.outildedecision.shared.user.repository.UserAvatarContentRepository;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.security.AppUserPrincipal;
import com.elit.outildedecision.shared.user.mapper.UserMapper;
import com.elit.outildedecision.shared.user.service.UserService;
import com.elit.outildedecision.shared.auth.service.VerificationMailService;
import com.elit.outildedecision.shared.auth.service.VerificationTokenService;
import com.elit.outildedecision.shared.util.Constants;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service implementation for managing user-related operations.
 */
@Service
@Transactional
@Slf4j
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
     * VerificationTokenService instance to manage verification tokens.
     */
    private final VerificationTokenService verificationTokenService;

    /**
     * Mapper converting user entities to response DTOs.
     */
    private final UserMapper userMapper;

    /**
     * Service composing and sending the registration confirmation e-mail.
     */
    private final VerificationMailService verificationMailService;

    /**
     * Publisher used to emit the user-registered event once the account is
     * persisted.
     */
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Repository for the binary content of user avatars
     * ({@code user_avatar_content} table).
     */
    private final UserAvatarContentRepository userAvatarContentRepository;

    /**
     * Logical avatar path prefix kept on {@code User.userAvatarPath} (no disk I/O).
     */
    @Value("${avatar.upload-dir}")
    private String avatarUploadDir;

    /**
     * Password policy: 12-32 chars, at least one lower, upper, digit and special
     * char, no whitespace.
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[~!@#$%^&*()_+`\\-=\\[\\]\\{\\};':\\\",./<>?])(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])\\S{12,32}$",
            Pattern.CASE_INSENSITIVE);

    /**
     * Maximum accepted avatar size in bytes (2 MiB): the whole file is held in
     * memory before being
     * persisted as a BLOB, so the upload is bounded well below the multipart limit.
     */
    private static final long MAX_AVATAR_SIZE_BYTES = 2L * 1024 * 1024;

    /**
     * Image MIME types accepted for a user avatar.
     */
    private static final List<String> ALLOWED_AVATAR_MIME_TYPES = List.of("image/png", "image/jpeg", "image/webp");

    /**
     * Constructor for UserServiceImpl.
     *
     * @param userRepository              UserRepository instance to interact with
     *                                    the database.
     * @param passwordEncoder             PasswordEncoder instance to encode and
     *                                    decode passwords.
     * @param verificationTokenService    VerificationTokenService instance to
     *                                    manage verification tokens.
     * @param userMapper                  UserMapper instance to convert user
     *                                    entities to response DTOs.
     * @param verificationMailService     Service composing and sending the
     *                                    registration confirmation e-mail.
     * @param eventPublisher              ApplicationEventPublisher used to emit the
     *                                    user-registered event.
     * @param userAvatarContentRepository Repository for the binary content of user
     *                                    avatars.
     */
    public UserServiceImpl(
            final UserRepository userRepository,
            final PasswordEncoder passwordEncoder,
            final VerificationTokenService verificationTokenService,
            final UserMapper userMapper,
            final VerificationMailService verificationMailService,
            final ApplicationEventPublisher eventPublisher,
            final UserAvatarContentRepository userAvatarContentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenService = verificationTokenService;
        this.userMapper = userMapper;
        this.verificationMailService = verificationMailService;
        this.eventPublisher = eventPublisher;
        this.userAvatarContentRepository = userAvatarContentRepository;
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
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {
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
     * Method to retrieve the permission labels held by a user on a given project.
     *
     * @param email     Email address of the authenticated user.
     * @param projectId Id of the project to check permissions for.
     * @return the permission labels held by the user on the project (possibly
     *         empty).
     */
    @Override
    public List<String> getProjectPermissions(final String email, final Long projectId) {
        return userRepository.findProjectPermissions(email, projectId);
    }

    /**
     * Method to retrieve the permission labels held by a user on the project owning
     * the given audit.
     *
     * @param email   Email address of the authenticated user.
     * @param auditId Id of the audit whose owning project is checked.
     * @return the permission labels held by the user on that project (possibly
     *         empty).
     */
    @Override
    public List<String> getProjectPermissionsByAudit(final String email, final Long auditId) {
        return userRepository.findProjectPermissionsByAuditId(email, auditId);
    }

    /**
     * Method to save new user to the database.
     *
     * @param user UserRequest object containing user information.
     * @return the success confirmation message.
     * @throws UserAlreadyExistsException if user already exists in the database
     */
    @Override
    public String saveUser(final UserRequest user)
            throws UserAlreadyExistsException, CouldNotSaveUserException {
        if (!user.getUserPassword()
                .equals(user.getUserPasswordConfirmation())) {
            throw new CouldNotSaveUserException(
                    "Les mots de passes ne sont pas identiques.");
        }
        if (userRepository.existsByUserEmail(user.getUserEmail())) {
            throw new UserAlreadyExistsException(
                    "Une erreur est survenue, le compte associé à cet e-mail existe déjà");
        }
        try {
            User userToSave = User.builder()
                    .userEmail(user.getUserEmail())
                    .isUserActive(false)
                    .isEmailVerified(false)
                    .username(user.getUsername())
                    .userFirstName(user.getUserFirstName())
                    .userLastName(user.getUserLastName())
                    .userPassword(passwordEncoder.encode(
                            user.getUserPassword()))
                    .isAdmin(false)
                    .build();
            User savedUser = userRepository.save(userToSave);
            VerificationToken token = verificationTokenService.createToken(savedUser, "confirm-account");
            log.info("User account created, userId={}", savedUser.getUserId());
            // Account + token are committed atomically here; the email is sent after commit
            // (UserRegistrationListener) so a mail failure cannot roll back the account and
            // the
            // slow polling stays out of this transaction.
            eventPublisher.publishEvent(new UserRegisteredEvent(savedUser.getUserId(), token.getToken()));
            return "Votre compte a été créé avec succès";
        } catch (RuntimeException e) {
            log.error("Failed to register user for email={}", user.getUserEmail(), e);
            throw new CouldNotSaveUserException(
                    "Une erreur est survenue lors de votre inscription. Veuillez réessayer plus tard");
        }
    }

    /**
     * Method to send mail to user to the database.
     *
     * @param user the user to send the registration confirmation email to again
     * @return the success confirmation message.
     * @throws ConflictException         if the account is already active
     * @throws CouldNotMailUserException if the confirmation email could not be sent
     */
    public String sendRegistrationConfirmationEmailAgain(
            final UserRequest user)
            throws CouldNotMailUserException {
        User userToMail = userRepository.findByUserEmail(user.getUserEmail());
        if (Boolean.TRUE.equals(userToMail.getIsUserActive())) {
            throw new ConflictException("Ce compte a déjà été activé");
        }
        try {
            sendRegistrationConfirmationEmail(userToMail);
            return "Le mail a correctement été envoyé";
        } catch (RuntimeException e) {
            log.error("Failed to resend confirmation email for email={}", user.getUserEmail(), e);
            throw new CouldNotMailUserException(
                    "Une erreur est survenue lors de l'envoi du mail. Veuillez réessayer plus tard");
        }
    }

    /**
     * Updates a user's password given their ID and old and new passwords.
     *
     * @param userId             The ID of the user to update.
     * @param oldPass            The user's current password.
     * @param newPass            The user's new password.
     * @param authenticatedEmail The email of the authenticated user (must own the
     *                           account).
     * @return the success confirmation message.
     */
    @Override
    public String updatePasswordUserById(final Long userId,
            final String oldPass,
            final String newPass,
            final String authenticatedEmail)
            throws UserNotFoundException, ForbiddenException {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        Constants.USER_NOT_FOUND));

        if (!user.getUserEmail().equals(authenticatedEmail)) {
            throw new ForbiddenException(
                    "Une erreur est survenue, votre nouveau mot de passe n’a pas été enregistré.");
        }

        final Matcher matcher = PASSWORD_PATTERN.matcher(newPass);
        if (!matcher.find()) {
            throw new BadRequestException(
                    "Le mot de passe doit contenir 12 caractères minimum dont 1 majuscule, 1 minuscule, 1 chiffre, 1 caractère spécial (~!@#$%^&*()_+`\\-=\\[\\]\\{\\};':\\\",./<>?) et pas d’espace.");
        }
        if (!passwordEncoder.matches(oldPass, user.getUserPassword())) {
            throw new BadRequestException("L'ancien mot de passe renseigné n'est pas correct.");
        }
        if (oldPass.equals(newPass)) {
            throw new BadRequestException(
                    "Le nouveau mot de passe renseigné ne doit pas être identique au précédent.");
        }
        user.setUserPassword(passwordEncoder.encode(newPass));
        userRepository.save(user);
        return "Votre nouveau mot de passe a bien été enregistré.";
    }

    /**
     * Updates a user's avatar given their ID and a new avatar file.
     *
     * @param userId     The ID of the user to update.
     * @param userAvatar The new avatar file.
     * @return the success confirmation message.
     * @throws UserNotFoundException If the specified user cannot be found.
     */
    @Override
    @Transactional
    public String updateAvatarByIdUser(final Long userId,
            final MultipartFile userAvatar)
            throws UserNotFoundException, CouldNotSaveUserException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        Constants.USER_NOT_FOUND));
        validateAvatar(userAvatar);
        try {
            String avatarPath = saveAvatar(userAvatar);
            user.setUserAvatarPath(avatarPath);
            userRepository.save(user);
            userAvatarContentRepository.save(new UserAvatarContent(userId, userAvatar.getBytes()));
            return "Votre photo a bien été modifiée.";
        } catch (IOException e) {
            log.error("Failed to save avatar for userId={}", userId, e);
            throw new CouldNotSaveUserException(
                    "Une erreur est survenue lors de l'enregistrement de votre photo.");
        }
    }

    /**
     * Validates an uploaded avatar before it is read into memory: the file must be
     * present, not
     * empty, within {@link #MAX_AVATAR_SIZE_BYTES} and carry an allowed image MIME
     * type.
     *
     * @param avatar the uploaded avatar file
     * @throws CouldNotSaveUserException if the file is missing, too large or not an
     *                                   allowed image
     */
    private void validateAvatar(final MultipartFile avatar)
            throws CouldNotSaveUserException {
        if (avatar == null || avatar.isEmpty()) {
            throw new CouldNotSaveUserException("Aucune photo n'a été fournie.");
        }
        if (avatar.getSize() > MAX_AVATAR_SIZE_BYTES) {
            throw new CouldNotSaveUserException(
                    "La photo de profil ne doit pas dépasser 2 Mo.");
        }
        String contentType = avatar.getContentType();
        if (contentType == null
                || !ALLOWED_AVATAR_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new CouldNotSaveUserException(
                    "Le format de la photo de profil n'est pas valide (formats acceptés : PNG, JPEG, WEBP).");
        }
    }

    /**
     * Updates a user's first name and/or last name given their ID.
     *
     * @param userId        The ID of the user to update.
     * @param userFirstName The user's new first name (maybe null).
     * @param userLastName  The user's new last name (maybe null).
     * @return the success confirmation message.
     * @throws CouldNotSaveUserException If there is an error saving the updated
     *                                   user to the database.
     * @throws UserNotFoundException     If the specified user cannot be found.
     */
    @Override
    @Transactional
    public String updateInfosByIdUser(final Long userId,
            final String userFirstName,
            final String userLastName)
            throws CouldNotSaveUserException, UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        Constants.USER_NOT_FOUND));
        user.setUserFirstName(userFirstName);
        user.setUserLastName(userLastName);
        try {
            userRepository.save(user);
        } catch (DataAccessException e) {
            log.error("Failed to update user info for userId={}", userId, e);
            throw new CouldNotSaveUserException(
                    "Erreur lors de l'enregistrement");
        }

        return "Vos modifications ont bien été enregistrées";
    }

    /**
     * Sends a user's avatar file to the client in the response body.
     *
     * @param userId   The ID of the user whose avatar should be sent.
     * @param response The response to write the avatar file to.
     * @throws IOException If there is an error reading or writing the avatar file.
     */
    @Override
    @Transactional(readOnly = true)
    public void viewAvatarByUserId(final Long userId,
            final HttpServletResponse response)
            throws IOException {
        User user = userRepository.getReferenceById(userId);
        String pathAvatar = user.getUserAvatarPath();
        UserAvatarContent avatarContent = userAvatarContentRepository.findById(userId).orElse(null);
        if (!Objects.isNull(pathAvatar) && avatarContent != null) {
            String fileName = StringUtils.getFilename(pathAvatar);
            String mimeType = URLConnection.guessContentTypeFromName(fileName);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            byte[] content = avatarContent.getContent();
            response.setContentType(mimeType);
            response.setHeader(
                    "Content-Disposition",
                    String.format("inline; filename=\"%s\"", fileName));
            response.setContentLength(content.length);
            response.getOutputStream().write(content);
            return;
        }
        log.warn(Constants.AVATAR_NOT_FOUND);
        response.getOutputStream().write(Constants.AVATAR_NOT_FOUND.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates a confirmation token for the user and sends the registration
     * confirmation email.
     *
     * <p>
     * Used by the resend flow, which owns its own transaction for the token
     * creation.
     *
     * @param user The user to send the email to.
     */
    @Override
    public void sendRegistrationConfirmationEmail(final User user) {
        VerificationToken verificationToken = verificationTokenService.createToken(user, "confirm-account");
        verificationMailService.sendAccountConfirmationEmail(user, verificationToken.getToken());
    }

    /**
     * Gets all available information about a user given their ID.
     *
     * @param userId The ID of the user to get information about.
     * @return A UserResponse object containing the requested information.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getAllInfosUserById(final Long userId) {
        log.debug("Fetching user information for userId={}", userId);
        User user = userRepository.getReferenceById(userId);
        return userMapper.toResponse(user);
    }

    /**
     * Finds users whose email or username contains a given keyword.
     *
     * @param keyword The keyword to search for.
     * @param onlyActive  when true, only active and email verified users are returned.
     * @return A list of UserResponse objects containing information about the found
     *         users.
     * @throws IOException If there is an error reading a user's avatar file.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findUsersByKeyword(final String keyword, final boolean onlyActive)
            throws IOException {
        String normalizedKeyword = (keyword == null || keyword.isEmpty()) ? null : keyword;
        return (onlyActive
            ? userRepository.findActiveUsersByKeyword(normalizedKeyword)
            : userRepository.findUsersByKeyword(normalizedKeyword))
                .stream()
                .map(this::createUserResponse)
                .toList();
    }

    /**
     * Finds all users whose email or username contains a given keyword.
     *
     * @param user The keyword to search for.
     * @return A UserResponse object.
     * @throws IOException If there is an error reading a user's avatar file.
     */
    private UserResponse createUserResponse(final User user) {
        UserResponse userResponse = userMapper.toResponse(user);

        if (user.getUserAvatarPath() != null) {
            userAvatarContentRepository.findById(user.getUserId())
                    .ifPresent(content -> userResponse.setUserAvatar(content.getContent()));
        }

        return userResponse;
    }

    /**
     * Builds the logical avatar path stored on the user (no disk I/O; the bytes are
     * persisted in the
     * {@code user_avatar_content} table by {@link #updateAvatarByIdUser}).
     *
     * @param avatar the avatar file uploaded by the user
     * @return the logical avatar path kept on {@code User.userAvatarPath}
     * @throws IOException never thrown (kept for interface compatibility)
     */
    @Override
    public String saveAvatar(final MultipartFile avatar) throws IOException {
        String folderPath = StringUtils.cleanPath(avatarUploadDir);
        return StringUtils.cleanPath(folderPath + avatar.getOriginalFilename());
    }

    /**
     * Deletes the avatar for the user with the specified ID.
     *
     * @param userId the ID of the user to update the avatar for
     * @return the success confirmation message.
     * @throws UserNotFoundException   if the specified user account does not exist
     * @throws AvatarNotFoundException if the specified avatar does not exist
     */
    @Override
    @Transactional
    public String deleteAvatarByIdUser(final Long userId)
            throws AvatarNotFoundException, UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        Constants.USER_NOT_FOUND));

        String pathAvatar = user.getUserAvatarPath();
        if (Objects.isNull(pathAvatar)) {
            throw new AvatarNotFoundException(Constants.AVATAR_NOT_FOUND);
        }

        if (userAvatarContentRepository.existsById(userId)) {
            userAvatarContentRepository.deleteById(userId);
        }
        user.setUserAvatarPath(null);
        userRepository.save(user);
        return "Photo de profil supprimé avec succès";
    }

}
