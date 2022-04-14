package com.mauro.chatvia.model;

import java.util.Date;

/**
 * Representation of incoming/outgoing chat messages.
 * @param from user sending the message.
 * @param message body of the message.
 * @param priv flag for private/public messages.
 * @param date date and time of sent message.
 */
public record ChatMessage(
        String from,
        String message,
        boolean priv,
        Date date
) {

    /**
     * Builds a new public message object
     * @param from user sending the message.
     * @param message body of the message.
     * @return new public message.
     */
    public static ChatMessage of(String from, String message) {
        return new ChatMessage(from, message, false, new Date());
    }

    /**
     * Builds a new private message object
     * @param from user sending the message.
     * @param message body of the message.
     * @return new private message.
     */
    public static ChatMessage priv(String from, String message) {
        return new ChatMessage(from, message, true, new Date());
    }

}
