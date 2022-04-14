package com.mauro.chatvia.config;

import com.mauro.chatvia.model.User;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.util.ArrayList;
import java.util.Map;

/**
 * User interceptor to extract "user" from header and fill acting Principal into the system.
 * This should be replaced by proper use of Spring security, and it's authN mechanism.
 */
public class UserInterceptor implements ChannelInterceptor {

    private static final String USER_HEADER_NAME = "username";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
            if (raw instanceof Map) {
                //Load username as user, as we don't have any authentication mechanism configured.
                Object name = ((Map<String, Object>) raw).get(USER_HEADER_NAME);
                if (name instanceof ArrayList) {
                    accessor.setUser(new User(((ArrayList<String>) name).get(0)));
                }
            }
        }
        return message;
    }
}
