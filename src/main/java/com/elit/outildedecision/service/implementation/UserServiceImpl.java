package com.elit.outildedecision.service.implementation;

import  com.elit.outildedecision.entity.User;
import com.elit.outildedecision.entity.VerificationToken;
import com.elit.outildedecision.exception.CouldNotSaveUserException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.UserModel;
import com.elit.outildedecision.model.UserResponseModel;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.security.AppUserPrincipal;
import com.elit.outildedecision.service.UserService;
import com.elit.outildedecision.service.VerificationTokenService;
import lombok.SneakyThrows;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.awt.print.Pageable;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    @Value("${avatar.upload-dir}")
    private String AVATAR_UPLOAD_DIR;
    private final PasswordEncoder passwordEncoder;
//    private final RoleRepository roleRepository;

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String SENDER_EMAIL;

    private final VerificationTokenService verificationTokenService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JavaMailSender javaMailSender, VerificationTokenService verificationTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
        this.verificationTokenService = verificationTokenService;
    }

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserEmailAndIsVerified(username);//Username(username);
        return new AppUserPrincipal(user);  // TODO : fix NullPointerException
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByUserEmail(email);
    }

    @Override
    public User findByUserId(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public ResponseEntity<String> saveUser(UserModel user) throws CouldNotSaveUserException {

        if (!user.getUserPassword().equals(user.getUserPasswordConfirmation())) {
            return new ResponseEntity<>("les mots de passe doivent être identiques", HttpStatus.BAD_REQUEST);
        }
        User userFromDB = userRepository.findByUseremailOrUsername(user.getUserEmail(), user.getUsername());
        if (!Objects.isNull(userFromDB)) {
            return new ResponseEntity<>("L'utilisateur existe déjà", HttpStatus.CONFLICT);
        }
        try {
            User userToSave = User.builder()
                    .userEmail(user.getUserEmail())
                    .isUserActive(false)
                    .isEmailVerified(false)
                    .username(user.getUsername())
                    .userFirstName(user.getUserFirstName())
                    .userLastName(user.getUserLastName())
                    .userPassword(passwordEncoder.encode(user.getUserPassword()))
                    .userJob(user.getUserJob())
                    .userAvatarPath(saveAvatar(user.getUserAvatar()))
                    .isAdmin(false)
                    .build();
            sendRegistrationConfirmationEmail(userRepository.save(userToSave));
        } catch (Exception e) {
            throw new CouldNotSaveUserException("Erreur lors de l'enregistrement de l'utilisateur");
        }
        return  ResponseEntity.status(HttpStatus.CREATED).body("Votre compte a été créé avec succès");
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public  ResponseEntity<String> updatePasswordUserById(Long userId, String oldPass, String newPass) throws  IOException, CouldNotSaveUserException {
        User user = userRepository.getById(userId);
        String currentPass = user.getUserPassword();
        if (Objects.isNull(user)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found user by this id");
        }

        Boolean checkPassword = passwordEncoder.matches(oldPass, currentPass);
        if (!checkPassword) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Votre ancien mot de passe n'est pas correct");
        } else {
            user.setUserPassword(passwordEncoder.encode(newPass));
        }
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body("Successful Update");
    }

    @Override
    public  ResponseEntity<String> updateAvatarByIdUser(Long userId, MultipartFile userAvatar) throws  IOException, CouldNotSaveUserException {
        User user = userRepository.getById(userId);
        if (Objects.isNull(user)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found user by this id");
        }

        if (!Objects.isNull(userAvatar)) {
            try {
                user.setUserAvatarPath(saveAvatar(userAvatar));
            } catch (Exception e) {
                throw new CouldNotSaveUserException("Erreur lors de l'enregistrement de l'avatar");
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body("Successful Update");
    }


    @Override
    public  ResponseEntity<String> updateInfosByIdUser(Long userId, String userFirstName, String userLastName) throws  IOException, CouldNotSaveUserException {
        User user = userRepository.getById(userId);
        if (Objects.isNull(user)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found user by this id");
        }

        if (!Objects.isNull(userFirstName)) {
            try {
                user.setUserFirstName(userFirstName);
            } catch (Exception e) {
                throw new CouldNotSaveUserException("Erreur lors de l'enregistrement de userFisrtName");
            }
        }

        if (!Objects.isNull(userLastName)) {
            try {
                user.setUserLastName(userLastName);
            } catch (Exception e) {
                throw new CouldNotSaveUserException("Erreur lors de l'enregistrement de userLastName");
            }
        }

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body("Successful Update");
    }


    @Override
    public void viewAvatarByUserId(Long userId, HttpServletResponse response) throws IOException {
        User user = userRepository.getById(userId);
        String pathAvatar = user.getUserAvatarPath();
        if (!Objects.isNull(pathAvatar)) {
            File file = new File(pathAvatar);
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            response.setContentType(mimeType);
            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
            response.setContentLength((int) file.length());

            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } else {
            System.out.println("L'avatar n'existe pas.");
        }
    }


    @Override
    public ResponseEntity<Map<String, String>> updateUser(UserModel user) throws CouldNotSaveUserException {
        String authenticatedUserEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userFromDB = userRepository.findByUserEmail(authenticatedUserEmail);
        Map<String, String> errorMap = new HashMap<>();
        if (Objects.isNull(userFromDB)) {
            errorMap.put("NOT_FOUND", "Utilisateur non trouvé");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
        }
        if (userRepository.findByUserEmail(user.getUserEmail()) != null && !userFromDB.getUserEmail().equals(user.getUserEmail())) {
            errorMap.put("CONFLICT", "email déjà utilisé");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMap);
        }
        if (!user.getUserPassword().equals(user.getUserPasswordConfirmation())) {
            errorMap.put("CONFLICT", "Les mots de passe doivent être identiques");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMap);
        }
        if (!user.getUserEmail().equals(user.getUserEmailConfirmation())) {
            errorMap.put("CONFLICT", "Les emails doivent être identiques");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMap);
        }
        if (userRepository.findByUsername(user.getUsername()) != null && !userFromDB.getUsername().equals(user.getUsername())) {
            errorMap.put("CONFLICT", "nom d'utilisateur déjà utilisé");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMap);
        }
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
        if (!Objects.isNull(user.getUserJob())) {
            userFromDB.setUserJob(user.getUserJob());
        }
        if (!Objects.isNull(user.getUserAvatar())) {
            try {
                userFromDB.setUserAvatarPath(saveAvatar(user.getUserAvatar()));
            } catch (Exception e) {
                throw new CouldNotSaveUserException("Erreur lors de l'enregistrement de l'avatar");
            }
        }
        userRepository.save(userFromDB);
        errorMap.put("SUCCESS", "Utilisateur mis à jour");
        return ResponseEntity.status(HttpStatus.OK).body(errorMap);
    }

    @Override
    public ResponseEntity<Object> uploadAttachment(File file, Long userId) throws UserNotFoundException {
        return null;
    }


    @Override
    public void sendRegistrationConfirmationEmail(User user) {
        //TODO : change he address of localhost to the real one

        VerificationToken verificationToken = verificationTokenService.createToken(user, "confirm-account");
        String confirmationUrl = "http://localhost:8080/confirm-account?token=" + verificationToken.getToken();
        String mailBody = "Bonjour "
                + " " + user.getUserFirstName() + " " + user.getUserLastName()
                + ",\n\n"
                + "Veuillez cliquer sur le lien ci-dessous pour confirmer votre compte :\n"
                + confirmationUrl
                + "\n\n"
                + "Cordialement,\n"
                + "L'équipe Outil de décision ELIT";
        String mailSubject = "ELIT : Confirmation de votre compte";
        try {
            // Creating a simple mail message
            SimpleMailMessage mailMessage  = new SimpleMailMessage();
            // Setting up necessary details
            mailMessage.setFrom(SENDER_EMAIL);
            mailMessage.setTo(user.getUserEmail());
            mailMessage.setText(mailBody);
            mailMessage.setSubject(mailSubject);

            // Sending the mail
            javaMailSender.send(mailMessage);
        }
        catch (Exception e) {
            throw new RuntimeException("Exception occured while sending email", e);
        }
    }

    @Override
    public List<User> findAllUsers() {
        return null;
    }

    @Override
    public List<User> findAllUsersByPage(Pageable pageable) {

        return null;
    }

    @Override
    public void confirmUser(User user) {
        user.setIsEmailVerified(true);
        user.setIsUserActive(true);
        userRepository.save(user);
    }

    @Override
    public UserResponseModel getAllInfosUserById(Long userId) throws IOException {
        User user = userRepository.getById(userId);
        System.out.println("fuhziuhfizuefh" + user.getUserId());
        if (user.getUserAvatarPath() != null) {
            File file = new File(user.getUserAvatarPath());
            UserResponseModel userResponseModel = UserResponseModel.builder()
                    .userId(user.getUserId())
                    .userFirstName(user.getUserFirstName())
                    .userLastName(user.getUserLastName())
                    .username(user.getUsername())
                    .userEmail(user.getUserEmail())
                    .userJob(user.getUserJob())
                    //.userAvatar(Files.readAllBytes(Paths.get(user.getUserAvatarPath())))
                    //.userAvatar(Files.readAllBytes(file.toPath()))
                    .build();
            return userResponseModel;

        } else {
            UserResponseModel userResponseModel = UserResponseModel.builder()
                    .userId(user.getUserId())
                    .userFirstName(user.getUserFirstName())
                    .userLastName(user.getUserLastName())
                    .username(user.getUsername())
                    .userEmail(user.getUserEmail())
                    .userJob(user.getUserJob())
                    //.userAvatar(Files.readAllBytes(Paths.get(user.getUserAvatarPath())))
                    .build();
            return userResponseModel;
        }
    }

    @Override
    public List<UserResponseModel> findUsersByKeyword(String keyword) throws IOException {
        List<UserResponseModel> foundUsers = new ArrayList<>();
        if (keyword == null || "".equals(keyword)) {
            for (User user : userRepository.findAll()) {
                if (user.getUserAvatarPath() != null) {
                    File file = new File(user.getUserAvatarPath());
                    UserResponseModel userResponseModel = UserResponseModel.builder()
                            .userId(user.getUserId())
                            .userFirstName(user.getUserFirstName())
                            .userLastName(user.getUserLastName())
                            .username(user.getUsername())
                            .userEmail(user.getUserEmail())
                            .userJob(user.getUserJob())
                            //.userAvatar(Files.readAllBytes(Paths.get(user.getUserAvatarPath())))
                            .userAvatar(Files.readAllBytes(file.toPath()))
                            .build();
                    foundUsers.add(userResponseModel);

                } else {
                    UserResponseModel userResponseModel = UserResponseModel.builder()
                            .userId(user.getUserId())
                            .userFirstName(user.getUserFirstName())
                            .userLastName(user.getUserLastName())
                            .username(user.getUsername())
                            .userEmail(user.getUserEmail())
                            .userJob(user.getUserJob())
                            //.userAvatar(Files.readAllBytes(Paths.get(user.getUserAvatarPath())))
                            .build();
                    foundUsers.add(userResponseModel);
                }
            }
            return foundUsers;
        }
        for (User user : userRepository.findAllByUserEmailOrUsernameContaining(keyword)) {
            File file = new File(user.getUserAvatarPath());
            UserResponseModel userResponseModel = UserResponseModel.builder()
                    .userFirstName(user.getUserFirstName())
                    .userLastName(user.getUserLastName())
                    .username(user.getUsername())
                    .userEmail(user.getUserEmail())
                    .userJob(user.getUserJob())
                    .userAvatar(Files.readAllBytes(Paths.get(user.getUserAvatarPath())))
                    .build();
            foundUsers.add(userResponseModel);
        }
        return foundUsers;
    }

    @Override
    public String saveAvatar(MultipartFile avatar) throws UserNotFoundException {

        String filePath;
        String folderPath = StringUtils.cleanPath(AVATAR_UPLOAD_DIR);
        try {
            filePath = StringUtils.cleanPath(folderPath + avatar.getOriginalFilename());
            Files.copy(avatar.getInputStream(), Path.of(filePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement de l'avatar");
        }
        return filePath;
    }


}
