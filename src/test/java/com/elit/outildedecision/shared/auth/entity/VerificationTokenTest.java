package com.elit.outildedecision.shared.auth.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VerificationTokenTest {
  @Test
  void testToString(){

   VerificationToken verificationToken = new VerificationToken();
   verificationToken.setToken("token");
   final String tokenString = "token";
   assertEquals(tokenString, verificationToken.getToken());
  }
}
