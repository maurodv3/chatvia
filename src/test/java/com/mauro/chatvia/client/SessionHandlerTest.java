package com.mauro.chatvia.client;

import com.mauro.chatvia.model.ChatMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class SessionHandlerTest {

    @Test
    public void testHandleFramePublic() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SessionHandler sessionHandler = new SessionHandler(new PrintStream(baos));
        sessionHandler.handleFrame(new StompHeaders(), ChatMessage.of("TestUser", "Hello!"));
        Assertions.assertTrue(baos.toString().contains("[TestUser]: Hello!"));
    }

    @Test
    public void testHandleFramePrivate() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SessionHandler sessionHandler = new SessionHandler(new PrintStream(baos));
        sessionHandler.handleFrame(new StompHeaders(), ChatMessage.priv("TestUser", "Private Hello!"));
        Assertions.assertTrue(baos.toString().contains("[TestUser]: Private Hello!"));
    }

    @Test
    public void testAfterConnected() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SessionHandler sessionHandler = new SessionHandler(new PrintStream(baos));
        StompSession session = Mockito.mock(StompSession.class);
        sessionHandler.afterConnected(session, new StompHeaders());
        Mockito.verify(session, Mockito.times(3)).subscribe(Mockito.anyString(), Mockito.any());
        System.out.println(baos.toString());
    }

}
