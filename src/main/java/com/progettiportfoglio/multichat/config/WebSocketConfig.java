package com.progettiportfoglio.multichat.config;

import com.progettiportfoglio.multichat.auth.TokenService;
import com.progettiportfoglio.multichat.security.TokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final TokenService tokenService;
    private final String frontendOrigin;

    public WebSocketConfig(TokenService tokenService,
                           @Value("${app.frontend-origin}") String frontendOrigin) {
        this.tokenService = tokenService;
        this.frontendOrigin = frontendOrigin;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins(frontendOrigin);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                var headers = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (headers == null || headers.getCommand() == null) {
                    return message; // Frame di controllo del protocollo.
                }

                if (headers.getCommand() == StompCommand.CONNECT) {
                    String token = TokenAuthenticationFilter.extractBearerToken(
                            headers.getFirstNativeHeader("Authorization"));
                    var user = tokenService.findUserByToken(token)
                            .orElseThrow(() -> new AccessDeniedException("Token non valido"));
                    headers.setUser(user);
                    return message;
                }

                if (headers.getCommand() == StompCommand.SUBSCRIBE) {
                    String destination = headers.getDestination();
                    if (headers.getUser() == null || destination == null
                            || !destination.matches("/topic/chats/[1-9][0-9]*/messages")) {
                        throw new AccessDeniedException("Sottoscrizione non consentita");
                    }
                    // TODO [WS-01]: estrarre chatId dalla destination e cercare la chat
                    // tramite un repository. Se non esiste, rifiutare la sottoscrizione.
                    return message;
                }

                if (headers.getCommand() == StompCommand.UNSUBSCRIBE
                        || headers.getCommand() == StompCommand.DISCONNECT) {
                    return message;
                }

                // I messaggi utente si inviano con POST REST. Un client non puo
                // pubblicare direttamente sul topic del broker saltando il DB.
                throw new AccessDeniedException("Operazione STOMP non consentita");
            }
        });
    }
}
