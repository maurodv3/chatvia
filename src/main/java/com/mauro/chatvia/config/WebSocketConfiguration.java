package com.mauro.chatvia.config;

import com.mauro.chatvia.controller.MessageController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Profile("server")
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketConfiguration.class);

    /**
     * Base destination prefix for messages mapping.
     */
    public static final String APP_DEST_PREFIX = "/app";

    /**
     * Base destination prefix for user destination mapping.
     */
    public static final String USER_DEST_PREFIX = "/user";

    /**
     * Stomp base endpoint for WS connection.
     */
    public static final String WS_CONNECTION_PATH = "/chat";

    /**
     * Base destination prefix for sending queues.
     */
    public static final String BROKER_QUEUE_DEST_PREFIX = "/queue";

    /**
     * Base destination prefix for sending topic.
     */
    public static final String BROKER_TOPIC_DEST_PREFIX = "/topic";

    private boolean externalRelayEnabled;

    private String relayHost;
    private int relayPort;
    private String relaySystemLogin;
    private String relaySystemPasscode;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        if (externalRelayEnabled) {
            config.enableStompBrokerRelay(BROKER_QUEUE_DEST_PREFIX, BROKER_TOPIC_DEST_PREFIX)
                    .setRelayHost(relayHost)
                    .setRelayPort(relayPort)
                    .setSystemLogin(relaySystemLogin)
                    .setSystemPasscode(relaySystemPasscode);
        } else {
            LOGGER.warn("Using in-memory SimpleBroker.");
            config.enableSimpleBroker(BROKER_QUEUE_DEST_PREFIX, BROKER_TOPIC_DEST_PREFIX);
        }
        config.setApplicationDestinationPrefixes(APP_DEST_PREFIX);
        config.setUserDestinationPrefix(USER_DEST_PREFIX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(WS_CONNECTION_PATH);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new UserInterceptor());
    }

    @Value("${broker.external.enabled:false}")
    public void setExternalRelayEnabled(boolean externalRelayEnabled) {
        this.externalRelayEnabled = externalRelayEnabled;
    }

    @Value("${broker.external.host:localhost}")
    public void setRelayHost(String relayHost) {
        this.relayHost = relayHost;
    }

    @Value("${broker.external.port:61613}")
    public void setRelayPort(int relayPort) {
        this.relayPort = relayPort;
    }

    @Value("${broker.external.system.user:guest}")
    public void setRelaySystemLogin(String relaySystemLogin) {
        this.relaySystemLogin = relaySystemLogin;
    }

    @Value("${broker.external.system.passcode:guest}")
    public void setRelaySystemPasscode(String relaySystemPasscode) {
        this.relaySystemPasscode = relaySystemPasscode;
    }
}
