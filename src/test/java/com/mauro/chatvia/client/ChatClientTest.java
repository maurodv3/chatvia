package com.mauro.chatvia.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import picocli.CommandLine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ChatClientTest {

    @Test
    public void testClientMissingParameter() {

        ChatClient chatClient = new ChatClient();
        CommandLine cmd = new CommandLine(chatClient);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));
        cmd.setErr(new PrintWriter(sw));

        cmd.execute();

        Assertions.assertTrue(sw.toString().contains("Missing required option: '--host=<host>'"));

    }

    @Test
    public void asd() throws ClientException {

        List<String> messages = new ArrayList<>();
        List<String> privMessages = new ArrayList<>();

        ChatService service = Mockito.mock(ChatService.class);
        Mockito.when(service.connect())
                .thenReturn(service);
        Mockito.doAnswer(invocation -> {
            messages.add(invocation.getArgument(0));
            return null;
        }).when(service).send(Mockito.anyString());
        Mockito.doAnswer(invocation -> {
            privMessages.add(invocation.getArgument(1));
            return null;
        }).when(service).sendTo(Mockito.anyString(), Mockito.anyString());
        ChatServiceFactory factory = Mockito.mock(ChatServiceFactory.class);
        Mockito.when(factory.getChatService(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(service);

        ChatClient chatClient = new ChatClient();
        chatClient.setChatServiceFactory(factory);

        byte[] input = String.join(System.lineSeparator(), List.of(
            "Message 1", "Message 2", "/whisper Test Message 3", "/whisper help", "/quit"
        )).getBytes();
        InputStream in = new ByteArrayInputStream(input);
        System.setIn(in);

        CommandLine cmd = new CommandLine(chatClient);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        cmd.execute("-h", "localhost");

        Assertions.assertEquals(messages.get(0), "Message 1");
        Assertions.assertEquals(messages.get(1), "Message 2");
        Assertions.assertEquals(privMessages.get(0), "Message 3");

    }
}
