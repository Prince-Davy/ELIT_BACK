package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.UserNotVerifiedException;
import com.elit.outildedecision.model.PasswordChangeModel;
import com.elit.outildedecision.service.VerificationTokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class VerificationController {
    private final VerificationTokenService verificationTokenService;

    public VerificationController(VerificationTokenService verificationTokenService) {
        this.verificationTokenService = verificationTokenService;
    }

    @GetMapping("/confirm-account")
    public ResponseEntity<String> confirmAccount(@RequestParam("token") String token) {
        return verificationTokenService.confirmUser(token);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> reinstateForgotPassword(@RequestParam("token") String token, @RequestBody PasswordChangeModel changePasswordModel) {
        return verificationTokenService.reinstateForgotPassword(token, changePasswordModel);
    }

    @GetMapping(value = "/reset-password", consumes = "application/json")
    public ResponseEntity<String> forgotPassword(HttpEntity<String> email) throws JsonProcessingException, UserNotVerifiedException {
        return verificationTokenService.forgotPassword(email.getBody());
    }
}
