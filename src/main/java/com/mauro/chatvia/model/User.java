package com.mauro.chatvia.model;

import java.security.Principal;

/**
 * Representation of chat user.
 * @param name of the chat user.
 */
public record User(String name) implements Principal {

    @Override
    public String getName() {
        return name;
    }

}
