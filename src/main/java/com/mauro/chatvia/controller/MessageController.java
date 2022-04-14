package com.mauro.chatvia.controller;

import com.mauro.chatvia.config.WebSocketConfiguration;
import com.mauro.chatvia.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class MessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

    public static final String GENERAL_MAPPING = "/messages";
    public static final String PRIVATE_MAPPING = "/private";
    public static final String ERROR_MAPPING = "/errors";

    /**
     * General mapping method, it receives and broadcast all incoming messages into general.
     * @param message to be sent.
     * @param principal user sending the message.
     * @return a message to be broadcast.
     */
    @MessageMapping(GENERAL_MAPPING)
    @SendTo(value = WebSocketConfiguration.BROKER_TOPIC_DEST_PREFIX + GENERAL_MAPPING)
    public ChatMessage generalMessages(
            @Payload ChatMessage message,
            Principal principal
    ) {
        LOGGER.info("Got message [{}] from [{}] in General.", message.message(), principal.getName());
        return ChatMessage.of(principal.getName(), message.message());
    }

    /**
     * Private mapping method, it receives and forwarded the incoming message to a connected user.
     * @param message to be sent.
     * @param principal user sending the message.
     * @return a message to be forwarded.
     */
    @MessageMapping(PRIVATE_MAPPING)
    @SendToUser(value = WebSocketConfiguration.BROKER_QUEUE_DEST_PREFIX + PRIVATE_MAPPING)
    public ChatMessage privateMessages(
            @Payload ChatMessage message,
            Principal principal
    )  {
        LOGGER.info("Got message [{}] from [{}] in Private.", message.message(), principal.getName());
        return ChatMessage.priv(principal.getName(), message.message());
    }

    /**
     * Error forwarder, it returns all the possible server errors caused by a user message to the owner.
     * @param exception being thrown
     * @return a message with error info.
     */
    @MessageExceptionHandler
    @SendToUser(destinations = WebSocketConfiguration.BROKER_QUEUE_DEST_PREFIX + ERROR_MAPPING, broadcast = false)
    public ChatMessage handleException(
            Exception exception
    ) {
        LOGGER.error("An error occurred sending message. Sending back to user.", exception);
        // This exposes the exception that might have sensitive info, this must be replaced with more user facing info.
        return ChatMessage.of("SYSTEM ERROR", exception.getMessage());
    }

}
