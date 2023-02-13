package com.elit.outildedecision.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.elit.outildedecision.model.ErrorMessage;
import com.elit.outildedecision.security.AppUserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final long EXPIRATION_TIME = 5*60*1000;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {

        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            Map<String, String> requestMap = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            String username = requestMap.get("username");
            String password = requestMap.get("password");
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage(),e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        AppUserPrincipal user = (AppUserPrincipal) authResult.getPrincipal(); // get the authenticated user
        Algorithm algorithm = Algorithm.HMAC256("ELIT2022".getBytes()); // our JWT secret is "ELIT2022" todo: change this
        String access_token = JWT.create()
                .withSubject(user.getUsername()) // the (email) of the authenticated user
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // the token will expire in 5min
                .withIssuer(request.getRequestURL().toString()) // the issuer of the token is the URL of the server
                .withClaim("roles",user.getAuthorities().stream().map(ga->ga.getAuthority()).collect(Collectors.toList())) // the roles of the authenticated user
                .withClaim("firstname",user.getUserFirstName()) // the first name of the authenticated user
                .withClaim("lastname",user.getUserLastName()) // the last name of the authenticated user
                .withClaim("id",user.getUser().getUserId()) // the user id of the authenticated user
                .sign(algorithm); // sign the token using our secret
        String refresh_token = JWT.create()
                .withSubject(user.getUsername()) // the username of the authenticated user
                .withExpiresAt(new Date(System.currentTimeMillis() + 8640*EXPIRATION_TIME)) // the token will expire in 30 days
                .withIssuer(request.getRequestURL().toString()) // the issuer of the token is the URL of the server
                .sign(algorithm); // sign the token using our secret
        Map<String, String> tokens = new HashMap<>();

        tokens.put("access-token", access_token);
        tokens.put("refresh-token", refresh_token);
//        response.addHeader("Access-Control-Allow-Origin", "*"); // this one allows to escape the CORS problem
        new ObjectMapper().writeValue(response.getOutputStream(), tokens); // send the token as JSON in the response body
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
//        super.unsuccessfulAuthentication(request, response, failed);
        Map<String, String> errors = new HashMap<>();
        errors.put("access-token", "access_token");
        errors.put("refresh-token", "refresh_token");
//        response.addHeader("Access-Control-Allow-Origin", "*"); // this one allows to escape the CORS problem
        response.sendError(1,"error");
        new ObjectMapper().writeValue(response.getOutputStream(), errors); // send the tok
    }

}
