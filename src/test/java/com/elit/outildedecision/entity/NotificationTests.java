package com.elit.outildedecision.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotificationTests {
  @Test
  void testToString() {
    Notification notification = new Notification();
    notification.setTitle("notifTitle");
    final String title = "notifTitle";
    assertEquals(title, notification.getTitle());

  }
}
