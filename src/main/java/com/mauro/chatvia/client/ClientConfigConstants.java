package com.mauro.chatvia.client;

import static com.mauro.chatvia.config.WebSocketConfiguration.*;
import static com.mauro.chatvia.controller.MessageController.*;

/**
 * Client configuration constants
 */
public final class ClientConfigConstants {

    private ClientConfigConstants() {}

    //Custom Stomp user header
    static final String USERNAME_STOMP_HEADER = "username";

    //WS connection endpoint.
    static final String WS_ENDPOINT = WS_CONNECTION_PATH;

    //General channel subscription path
    static final String GENERAL_CHANNEL_SUB = BROKER_TOPIC_DEST_PREFIX + GENERAL_MAPPING;

    //Private channel subscription path
    static final String PRIVATE_CHANNEL_SUB = USER_DEST_PREFIX + BROKER_QUEUE_DEST_PREFIX + PRIVATE_MAPPING;

    //Error channel subscription path
    static final String ERROR_CHANNEL_SUB = USER_DEST_PREFIX + BROKER_QUEUE_DEST_PREFIX + ERROR_MAPPING;

    //General channel publish path
    static final String GENERAL_CHANNEL_PUB = APP_DEST_PREFIX + GENERAL_MAPPING;

    //Private channel publish path
    static final String PRIVATE_CHANNEL_PUB = USER_DEST_PREFIX + "/%s" + BROKER_QUEUE_DEST_PREFIX + PRIVATE_MAPPING;

}
