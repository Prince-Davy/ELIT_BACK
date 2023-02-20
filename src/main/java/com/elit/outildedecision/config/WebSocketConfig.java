package com.elit.outildedecision.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  /**
   * cette methode permet de configurer les endpoints de websockets.
   */
  @Override
  public void registerStompEndpoints(final StompEndpointRegistry registry) {
    registry
        .addEndpoint("/mywebsockets/**")
        // allow all origins like CORS
        .setAllowedOriginPatterns("*")
        // this allows our webSockets works even if the browser doesn't support
        .withSockJS();
  }
  /**
   * cette methode permet de configurer le broker de message.
   */
  @Override
  public void configureMessageBroker(final MessageBrokerRegistry registry) {
    /*
     * the prefix /app is used to prefix the messages sent from the client to the
     * server.
     * and those messages will be handeled by the MessageController. annotated
     * with @MessageMapping("/some-route")
     */
    registry.setApplicationDestinationPrefixes("/app");
    /*
     * messages that are carried on to all subscribers are prefixed with /topic
     * on the other hand messages that are sent to a specific subscriber are
     * prefixed with /queue
     */
    registry.enableSimpleBroker("/topic/");
  }
}
