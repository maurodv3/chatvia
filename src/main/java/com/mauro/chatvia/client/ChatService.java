package com.mauro.chatvia.client;

import com.mauro.chatvia.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.PrintStream;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static com.mauro.chatvia.client.ClientConfigConstants.*;
import static com.mauro.chatvia.client.ClientMessages.DATE_FORMAT;
import static com.mauro.chatvia.client.ClientMessages.PRIVATE_MSG_PATTERN;

public class ChatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    private final String url;
    private final String username;
    private final PrintStream output;

    private StompSession currentSession;

    public ChatService(String url, String username, PrintStream output) {
        this.url = url;
        this.username = username;
        this.output = output;
    }

    /**
     * Initializes the chat client.
     * @return a connected chat client.
     * @throws ClientException if connection fails.
     */
    public ChatService connect() throws ClientException {
        WebSocketClient wsClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(wsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.setTaskScheduler(new DefaultManagedTaskScheduler());

        StompSessionHandler sessionHandler = new SessionHandler(output);

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add(USERNAME_STOMP_HEADER, username);
        StompSession session;
        try {
            session = stompClient
                    .connect(url, new WebSocketHttpHeaders(), stompHeaders, sessionHandler)
                    .get();
            session.setAutoReceipt(true);
        } catch (InterruptedException e) {
            LOGGER.error("Connection thread was interrupted, connection failed.", e);
            throw new ClientException(url, username, "Cannot connect to the server.");
        } catch (ExecutionException e) {
            LOGGER.error("Connection failed.", e);
            throw new ClientException(url, username, "Cannot connect to the server.");
        }

        setCurrentSession(session);
        return this;
    }

    /**
     * Sends a public message to the general channel.
     * @param message to send.
     * @throws ClientException if connection fails.
     */
    public void send(String message) throws ClientException {
        handleReconnection();
        currentSession.send(GENERAL_CHANNEL_PUB, ChatMessage.of(username, message));
    }

    /**
     * Sends a private message to specified user.
     * @param user to receive the message.
     * @param message to send.
     * @throws ClientException if connection fails.
     */
    public void sendTo(String user, String message) throws ClientException {
        handleReconnection();
        currentSession.send(PRIVATE_CHANNEL_PUB.formatted(user), ChatMessage.priv(username, message))
                .addReceiptTask(() -> {
                    //If received print local message.
                    String formattedTime = DATE_FORMAT.format(new Date());
                    output.printf(PRIVATE_MSG_PATTERN, formattedTime, username, message);
                });
    }

    private void setCurrentSession(StompSession currentSession) {
        this.currentSession = currentSession;
    }

    /**
     * Tests current session for connection status, will retry connection if not connected.
     * @throws ClientException if connection fails.
     */
    private void handleReconnection() throws ClientException {
        if (!currentSession.isConnected()) {
            LOGGER.info("Client is reconnecting.");
            this.connect();
        }
    }
}

