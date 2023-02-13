package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.ProjectPropertyKey;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    private static final String CHAT_TOPIC = "/topic/chatroom";

    /**
     * this controller is used to handle the messages sent from the client to the server. using /app/chatroom as the route
     * after the treatement the message will be sent to the broker
     * @param message
     * @return
     */
    @MessageMapping("/chatroom")
    @SendTo(CHAT_TOPIC) // we can also use the SimpMessagingTemplate to send messages to the client
    public ProjectPropertyKey.Message receivePublicMessage(@Payload ProjectPropertyKey.Message message) {
        System.out.println("message = " + message);
        return message;
    }



}
