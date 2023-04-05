package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * The chat message class.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
  private String username;
    private String avatar;
    private String messageBody;
    private String status;
}
