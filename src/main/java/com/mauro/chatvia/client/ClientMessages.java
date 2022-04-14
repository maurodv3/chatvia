package com.mauro.chatvia.client;

import java.text.SimpleDateFormat;

/**
 * User facing messages.
 */
public final class ClientMessages {

    private ClientMessages() {}

    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    static final String WELCOME_MSG = "Welcome to the chat!.";
    static final String WHISPER_HELP_MSG = "Private messaging command: /whisper <user> <your message>";
    static final String WRITE_HELP_MSG = "Type below ...";
    static final String GENERAL_MSG_PATTERN = "%s - [%s]: %s%n";
    static final String PRIVATE_MSG_PATTERN = "Private - %s - [%s]: %s%n";

}
