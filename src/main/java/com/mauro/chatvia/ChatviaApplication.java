package com.mauro.chatvia;

import com.mauro.chatvia.client.ChatClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootApplication
public class ChatviaApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(StompSessionHandlerAdapter.class);

	private Set<String> activeProfiles;
	private ChatClient chatClient;

	public static void main(String[] args) {
		SpringApplication.run(ChatviaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (activeProfiles.contains("client")) {
			//This will launch the client application, as part of the same spring boot app.
			//This is not ideal, as bundling both server and client causes to have a heavier client, and also
			//increase the complexity of the server/client separation of the spring components.
			LOGGER.info("Running APP as CLIENT mode.");
			int exitCode = new CommandLine(chatClient).execute(args);
			System.exit(exitCode);
		}
		if (activeProfiles.contains("server")) {
			LOGGER.info("Running APP as SERVER mode.");
		}
	}

	@Value("${spring.profiles.active:}")
	public void setActiveProfiles(String activeProfiles) {
		this.activeProfiles = Arrays.stream(activeProfiles.split(","))
				.collect(Collectors.toSet());
	}

	@Autowired
	public void setChatClient(ChatClient chatClient) {
		this.chatClient = chatClient;
	}
}
