package com.elit.outildedecision.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/mywebsockets/**")
//                .setAllowedOrigins("*")
                .setAllowedOriginPatterns("*")// allow all origins like CORS
                .withSockJS(); // this allows our webSockets works even if the browser doesn't support webSockets protocol
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        /*
        *the prefix /app is used to prefix the messages sent from the client to the server.
        * and those messages will be handeled by the MessageController. annotated with @MessageMapping("/some-route")
         */
       registry.setApplicationDestinationPrefixes("/app");
        /*messages that are carried on to all subscribers are prefixed with /topic
         on the other hand messages that are sent to a specific subscriber are prefixed with /queue */
         registry.enableSimpleBroker("/topic/");
    }
}
