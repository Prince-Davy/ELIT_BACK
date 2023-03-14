package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * this class is used to store email details.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {
  private String recipient;
  private String msgBody;
  private String subject;
  private String attachment;
}
