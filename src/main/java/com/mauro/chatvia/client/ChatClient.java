package com.mauro.chatvia.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.UUID;

import static com.mauro.chatvia.client.ClientConfigConstants.WS_ENDPOINT;
import static com.mauro.chatvia.client.ClientMessages.WHISPER_HELP_MSG;
import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

/**
 * Base command for Chat Client execution.
 * Reads the required options and starts an idle scan for outgoing messages and commands.
 */
@Command(mixinStandardHelpOptions = true, description = "Start Chat Client")
public class ChatClient implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatClient.class);

    private static final String EXIT_CMD = "/quit";
    private static final String WHISPER_CMD = "/whisper "; // we split on whitespace so keep as it is necessary.

    @Option(names = {"-h", "--host"}, description = "Server URL", required = true)
    private String host  = "localhost";

    @Option(names = {"-n", "--name"} , description = "User name")
    private String name = UUID.randomUUID().toString();

    @Override
    public void run() {
        try {
            _run();
        } catch (ClientException e) {
            LOGGER.error("Error on client:", e);
        }
    }

    private void _run() throws ClientException {

        final String url = buildURL(host);

        ChatService chat = new ChatService(url, name, System.out)
                .connect();

        //Read messages until quit.
        Scanner scanner = new Scanner(System.in);
        String message = scanner.nextLine();
        while (!message.equals(EXIT_CMD)) {

            if (message.startsWith(WHISPER_CMD)) {
                handleWhisper(chat, message);
                message = scanner.nextLine();
                continue;
            }

            chat.send(message);
            message = scanner.nextLine();
        }
    }

    /**
     * Handles the whisper command and sends to different publish queue, driven by the message extracted commands.
     * @param chat current chat session.
     * @param command containing the destination users and the message.
     */
    private void handleWhisper(ChatService chat, String command) throws ClientException {
        String [] whisperParams = command.split(" ", 3);
        if (whisperParams.length != 3) {
            System.out.println(WHISPER_HELP_MSG);
            return;
        }
        final String targetUser = whisperParams[1];
        final String message = whisperParams[2];
        chat.sendTo(targetUser, message);
    }

    private String buildURL(String host) {
        return "ws://" + host + ":3333/" + WS_ENDPOINT;
    }

}
