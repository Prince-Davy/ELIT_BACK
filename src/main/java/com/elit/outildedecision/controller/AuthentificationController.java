package com.elit.outildedecision.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.CouldNotSaveUserException;
import com.elit.outildedecision.model.UserModel;
import com.elit.outildedecision.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
public class AuthentificationController {
    private final UserService userService;
    private static final long EXPIRATION_TIME = 5*60*1000;

public AuthentificationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserModel user) throws CouldNotSaveUserException {
        return userService.saveUser(user);
    }

    @GetMapping("/refreshToken")
    public void refreshToken (HttpServletRequest request, HttpServletResponse response) throws IOException {
        String jwtAuthorizationToken = request.getHeader("Authorization");
        if(jwtAuthorizationToken !=null && jwtAuthorizationToken.startsWith("Bearer ")){
            try{

                String refresh_token = jwtAuthorizationToken.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("ELIT2022".getBytes()); // our JWT secret is "ELIT2022" todo: change this
                JWTVerifier verifier = JWT.require(algorithm).build(); //Require an algorithm and a secret:
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String userEmail = decodedJWT.getSubject();
                User user = userService.findByEmail(userEmail);
                String access_token = JWT.create()
                        .withSubject(user.getUserEmail()) // the username of the authenticated user
                        .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // the token will expire in 5min
                        .withIssuer(request.getRequestURL().toString()) // the issuer of the token is the URL of the server
                        .withClaim("roles", List.of(user.getIsAdmin()? "ADMIN" : "USER")) // the roles of the authenticated user
                        .withClaim("firstname",user.getUserFirstName()) // the first name of the authenticated user
                        .withClaim("lastname",user.getUserLastName()) // the last name of the authenticated user
                        .withClaim("id",user.getUserId()) // the user id of the authenticated user
                        .sign(algorithm); // sign the token using our secret

               Map<String, String> tokens = new HashMap<>();
                System.out.println("List.of(user.getIsAdmin()?\"ADMIN\":\"USER\") = " + List.of(user.getIsAdmin()?"ADMIN":"USER"));
               tokens.put("access-token", access_token);
               tokens.put("refresh-token", refresh_token);
               response.setContentType(MediaType.APPLICATION_JSON_VALUE);
               new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            }catch (Exception e){
            response.setHeader("error", e.getMessage());
            response.setStatus(FORBIDDEN.value());
            Map<String, String> error_message = new HashMap<>();
            error_message.put("error-message", e.getMessage());
            response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), error_message);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }
}
