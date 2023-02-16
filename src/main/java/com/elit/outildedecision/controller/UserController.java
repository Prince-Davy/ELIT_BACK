package com.elit.outildedecision.controller;


import com.elit.outildedecision.exception.CouldNotSaveUserException;
import com.elit.outildedecision.model.UserModel;
import com.elit.outildedecision.model.UserResponseModel;
import com.elit.outildedecision.repository.UserRepository;
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

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService,
            UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/user/users")
    public List<UserResponseModel> getUsersByKeyword(@RequestParam(value = "keyword", required = false) String keyword) throws IOException {
        return userService.findUsersByKeyword(keyword);
    }

    @PutMapping(value = "/user/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, String>> updateUser(@Valid @ModelAttribute UserModel userModel) throws CouldNotSaveUserException {
        return userService.updateUser(userModel);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public UserResponseModel getUserById(@PathVariable Long userId) throws IOException {
        return userService.getAllInfosUserById(userId);
    }

    @PutMapping(value = "/user/password/{userId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<String> updatePasswordUserById(@PathVariable Long userId, @RequestParam String oldPass, @RequestParam String newPass) throws IOException, CouldNotSaveUserException {
        return userService.updatePasswordUserById(userId, oldPass, newPass);
    }

    @PutMapping(value = "/user/avatar/{userId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> updateAvatarByIdUser(@PathVariable Long userId, @RequestParam MultipartFile userAvatar) throws IOException, CouldNotSaveUserException {
        return userService.updateAvatarByIdUser(userId, userAvatar);
    }

    @PutMapping(value = "/user/info/{userId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> updateInfosByIdUser(@PathVariable Long userId, @RequestParam String userFirstName, @RequestParam String userLastName) throws IOException, CouldNotSaveUserException {
        return userService.updateInfosByIdUser(userId, userFirstName, userLastName);
    }

    @RequestMapping("/user/avatar/{userId}")
    public void viewAvatarByUserId(HttpServletResponse response, @PathVariable Long userId) throws IOException {
        userService.viewAvatarByUserId(userId, response);
    }


}
