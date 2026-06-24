package com.elit.outildedecision.shared.project.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotificationTest {
  @Test
  void testToString() {
    Notification notification = new Notification();
    notification.setTitle("notifTitle");
    final String title = "notifTitle";
    assertEquals(title, notification.getTitle());

  }
}
