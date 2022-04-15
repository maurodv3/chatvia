package com.mauro.chatvia.client;

import org.springframework.stereotype.Component;

import java.io.PrintStream;

@Component
public class ChatServiceFactory {

    public ChatService getChatService(String url, String username, PrintStream output) {
        return new ChatService(url, username, output);
    }

}
