package com.mauro.chatvia.client;

/**
 * Wrapper exception for client errors containing connection information.
 */
public class ClientException extends Exception {

    private final String url;
    private final String user;

    public ClientException(String message, String url, String user) {
        super(message);
        this.url = url;
        this.user = user;
    }
}
