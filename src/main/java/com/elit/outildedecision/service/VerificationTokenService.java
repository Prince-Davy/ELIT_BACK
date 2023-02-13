package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.entity.VerificationToken;
import com.elit.outildedecision.exception.UserNotVerifiedException;
import com.elit.outildedecision.model.PasswordChangeModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

public interface VerificationTokenService {
    VerificationToken findByToken(String token);

    void removeByToken(String token);

    VerificationToken createToken(User user, String type);

    ResponseEntity<String> confirmUser(String token);

    ResponseEntity<String> reinstateForgotPassword(String token, PasswordChangeModel changePasswordModel);

    ResponseEntity<String> forgotPassword(String email) throws JsonProcessingException, UserNotVerifiedException;
}
