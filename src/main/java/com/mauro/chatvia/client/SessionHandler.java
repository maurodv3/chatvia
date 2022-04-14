package com.mauro.chatvia.client;

import com.mauro.chatvia.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.io.PrintStream;
import java.lang.reflect.Type;

import static com.mauro.chatvia.client.ClientConfigConstants.*;
import static com.mauro.chatvia.client.ClientMessages.*;

/**
 * Session handler for adding custom functionality to Stomp session events.
 */
public class SessionHandler extends StompSessionHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(StompSessionHandlerAdapter.class);

    private final PrintStream output;

    public SessionHandler(PrintStream output) {
        this.output = output;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ChatMessage.class;
    }

    /**
     * Handles all incoming messages from the server.
     * @param headers being received as part of the message.
     * @param payload is actual message received from the server.
     */
    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        if (payload == null) {
            LOGGER.info("Payload is null: " + headers);
            return;
        }
        ChatMessage msg = (ChatMessage) payload;
        String formattedTime = DATE_FORMAT.format(msg.date());
        if (msg.priv()) {
            output.printf(PRIVATE_MSG_PATTERN, formattedTime, msg.from(), msg.message());
        } else {
            output.printf(GENERAL_MSG_PATTERN, formattedTime, msg.from(), msg.message());
        }
    }

    /**
     * Post connection required executions, this needs to be run for chat to proper function.
     * It handles all the necessary subscriptions to different channels and prints information for usage.
     * @param session freshly created connection.
     * @param connectedHeaders headers of the new session.
     */
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        LOGGER.debug("Session established: [{}].", session.getSessionId());

        session.subscribe(GENERAL_CHANNEL_SUB, this);
        session.subscribe(ERROR_CHANNEL_SUB, this);
        session.subscribe(PRIVATE_CHANNEL_SUB, this);

        LOGGER.debug("Subscribed to successfully.");

        output.println(WELCOME_MSG);
        output.println(WHISPER_HELP_MSG);
        output.println(WRITE_HELP_MSG);
    }

    /**
     * Connection error handler, logs all the possible errors with the server/client connection.
     * @param session being executed at error.
     * @param command action being executed at error.
     * @param headers part of the error request.
     * @param payload part of the error request.
     * @param exception raised by the client.
     */
    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        LOGGER.error("Error:", exception);
    }

}
