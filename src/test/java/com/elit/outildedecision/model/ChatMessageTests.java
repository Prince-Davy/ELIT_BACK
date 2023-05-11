package com.elit.outildedecision.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatMessageTests {

  @Test
  void getAndSetTests(){
    final String username = "username";
    final String avatar = "";
    final String messageBody ="messageBody";
    final String status = "status";
    ChatMessage cm = new ChatMessage();
    cm.setUsername(username);
    cm.setMessageBody(messageBody);
    cm.setAvatar(avatar);
    cm.setStatus(status);
    assertEquals(cm.getMessageBody(),messageBody);
    assertEquals(cm.getAvatar(),avatar);
    assertEquals(cm.getStatus(),status);
    assertEquals(cm.getUsername(),username);

  }
}
