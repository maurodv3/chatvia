# Chatvia

[![<maurodv3>](https://circleci.com/gh/maurodv3/chatvia.svg?style=shield)](<LINK>)

## Simple Chat server and client, made in Java and Spring.

The application works using [Websockets](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API) for communicating the server and different clients maintaining an open connection between both parties. For this it makes use of the [STOMP](https://stomp.github.io/stomp-specification-1.2.html) sub-protocol over Websocket for simplified communication.
Using this technology we can share messages between clients at real-time and avoid pooling from messages on the client at the cost of having more open connections in the server.

### Build the project
```bash
./gradlew build
```
This will package the server/client code into a jar file.

### Run locally

Server Usage:
```bash
./scripts/chatserver
```
Client Usage:
```bash
./scripts/chatclient -h localhost
```

### Run using docker

```bash
./scripts/chatserver-compose
```

Starts the chat server into a docker container, this will also start the server into external broker mode and will use RabbitMQ to message exchange ([See: Server modes](#server-modes)) this configuration is necessary for server clustering.

---

### Server Modes

The server can run in two different modes:
* Simple broker: This mode starts an in-memory broker with limited functionality, mostly used for development.
* External broker: This mode uses an existing running broker and connects to it, this mode allows multiple chat services working and also provides message confirmation and the full features provided and configured in the broker.

#### External mode configuration variables

```properties
# Flips between simple/external broker mode.
BROKER_ENABLED=true/false 
# Broker location
BROKER_HOST=localhost
# Broker STOMP port
BROKER_PORT=61613
# Broker System user for authentication
BROKER_SYSTEM_USER=guest
# Broker System user passcode for authentication
BROKER_SYSTEM_PASSCODE=guest
```

### Useful links
* [Spring Websocket Docs](https://docs.spring.io/spring-framework/docs/4.3.x/spring-framework-reference/html/websocket.html)
* [STOMP Protocol](https://stomp.github.io)
* [Picocli](https://picocli.info)
* [RabbitMQ Stomp Plugin](https://www.rabbitmq.com/stomp.html)

