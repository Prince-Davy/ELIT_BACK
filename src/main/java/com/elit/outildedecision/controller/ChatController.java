
package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.ProjectPropertyKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

/**
 * The chat controller.
 */
@RestController
public class ChatController {
  private final Logger log = LoggerFactory.getLogger(ChatController.class);

  /**
   * the topic to which the messages will be sent.
   */
  private static final String CHAT_TOPIC = "/topic/chatroom";

  /**
   * this controller is used to handle the messages sent from the client to the server. using
   * /app/chatroom as the route after the treatement the message will be sent to the broker
   *
   * @param message the message sent from the client
   * @return the message to be sent to the client
   */
  @MessageMapping("/chatroom")
  @SendTo(CHAT_TOPIC) // we can also use the SimpMessagingTemplate to send messages to the client
  public ProjectPropertyKey.Message receivePublicMessage(
      @Payload final ProjectPropertyKey.Message message) {
    this.log.info("message : {}" , message);
    return message;
  }
}
