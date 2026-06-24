package com.elit.outildedecision.shared.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Simple message dto class. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleMessageDto {
  private String message;
  private int status;
  private LocalDateTime timestamp;
}
