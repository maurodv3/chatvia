package com.mauro.chatvia;

import com.mauro.chatvia.client.ChatService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ChatviaApplicationTest {

	private final ExecutorService executorService = Executors.newFixedThreadPool(2);

	@Test
	void testClientServerInteraction() throws ExecutionException, InterruptedException {

		final ByteArrayOutputStream baosUserOne = new ByteArrayOutputStream();
		final ByteArrayOutputStream baosUserTwo = new ByteArrayOutputStream();

		CompletableFuture<Boolean> u1 = CompletableFuture.supplyAsync(() -> {
			try {
				PrintStream psUserOne = new PrintStream(baosUserOne);
				ChatService chatForUserOne = new ChatService(testUrl(), "One", psUserOne);
				chatForUserOne.connect();
				chatForUserOne.send("Testing with User One");
				return true;
			} catch (Exception e) {
				return false;
			}
		}, executorService);

		CompletableFuture<Boolean> u2 = CompletableFuture.supplyAsync(() -> {
			try {
				PrintStream psUserTwo = new PrintStream(baosUserTwo);
				ChatService chatForUserTwo = new ChatService(testUrl(), "Two", psUserTwo);
				chatForUserTwo.connect();
				chatForUserTwo.send("Testing with User Two");
				return true;
			} catch (Exception e) {
				return false;
			}
		}, executorService);

		CompletableFuture<Boolean> wait = CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(3000);
				return true;
			} catch (Exception e) {
				return false;
			}
		}, executorService);

		//Message distribution take sometime after the client send. Wait a few seconds to validate output.
		Assertions.assertTrue(u1.get(), "Error on User One.");
		Assertions.assertTrue(u2.get(), "Error on User Two.");
		Assertions.assertTrue(wait.get());

		//Parse chat output.
		Set<String> userOneOutput = Stream.of(baosUserOne.toString().split("\n"))
				.collect(Collectors.toSet());
		Set<String> userTwoOutput = Stream.of(baosUserTwo.toString().split("\n"))
				.collect(Collectors.toSet());

		Assertions.assertEquals(5, userOneOutput.size());
		Assertions.assertEquals(5, userTwoOutput.size());

		//Validate User One has both messages.
		var msg1 = userOneOutput.stream().filter(s -> s.contains("[One]: Testing with User One"))
				.findAny()
				.orElse(null);
		var msg2 = userOneOutput.stream().filter(s -> s.contains("[Two]: Testing with User Two"))
				.findAny()
				.orElse(null);

		Assertions.assertNotNull(msg1, "Message missing for user 1.");
		Assertions.assertNotNull(msg2, "Message missing for user 1.");

		//Validate User Two has both messages.
		var msg3 = userTwoOutput.stream().filter(s -> s.contains("[One]: Testing with User One"))
				.findAny()
				.orElse(null);
		var msg4 = userTwoOutput.stream().filter(s -> s.contains("[Two]: Testing with User Two"))
				.findAny()
				.orElse(null);

		Assertions.assertNotNull(msg3, "Message missing for user 2.");
		Assertions.assertNotNull(msg4, "Message missing for user 2.");

	}

	@Test
	private String testUrl() {
		return "ws://localhost:3334/chat";
	}

}
