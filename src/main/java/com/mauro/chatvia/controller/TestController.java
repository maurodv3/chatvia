package com.mauro.chatvia.controller;

import com.mauro.chatvia.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Test controller for sending general/private messages to subjects.
 */
@Controller("/test")
@Profile("dev")
public class TestController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public TestController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/general")
    public ResponseEntity<String> generalMessage(
            @RequestBody String message
    ) {
        messagingTemplate.convertAndSend("/queue/messages", ChatMessage.of("SYSTEM", message));
        return ResponseEntity.ok("SENT");
    }

    @PostMapping("/private/{user}")
    public ResponseEntity<String> privateMessage(
            @PathVariable("user") String user,
            @RequestBody String message
    ) {
        messagingTemplate.convertAndSendToUser(user, "/queue/private", ChatMessage.of("SYSTEM", message));
        return ResponseEntity.ok("SENT");
    }

}
