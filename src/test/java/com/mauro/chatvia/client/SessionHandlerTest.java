package com.mauro.chatvia.client;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class SessionHandlerTest {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    SessionHandler sessionHandler = new SessionHandler(new PrintStream(baos));

}
