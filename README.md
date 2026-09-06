# Java TCP Chat Room

A terminal-based group chat application built with Java sockets. A host accepts multiple clients, broadcasts messages and handles private messages and administrator commands.

## Run locally

Install a Java Development Kit (JDK), then clone and compile the project:

```bash
git clone https://github.com/Akrouma03/Java-TCP-Chat-room.git
cd Java-TCP-Chat-room
javac Host.java Client.java
java Host
```

Keep the host running. Open a second terminal in the same directory:

```bash
java Client
```

Enter an ID when prompted. Repeat `java Client` in another terminal to add a participant. The client connects to `127.0.0.1:9999`, so this setup runs on one computer. The first connected user receives administrator privileges.

## Commands

Commands are case-sensitive. Replace angle-bracket placeholders with your own values.

| Command | Action |
| --- | --- |
| `/changeID <ID>` | Change your display ID. |
| `/pm <ID> <message>` | Send a private message to another user. |
| `/info` | Display your ID and connection information. |
| `/kick <ID>` | Disconnect a user; requires administrator privileges. |
| `/quit` | Leave the chat. |

Any other text is broadcast as a group message. Use single-word IDs for private-message recipients.

## Try it

Connect two clients, using `Alice` for the first and `Bob` for the second. From Alice's terminal, send:

```text
Hello everyone!
/pm Bob Hello Bob!
/info
```

Bob should receive the group message and the private message. Use `/quit` to leave, and Ctrl+C in the host terminal to stop the server.

## Structure

- `Host.java`: accepts connections, manages clients and routes messages.
- `Client.java`: reads server messages and sends terminal input on a separate thread.

## Scope

An educational networking project. Messages use plain TCP, with no encryption, authenticated accounts or persistent message history. The supplied configuration is intended for a local demonstration.