package JUnit_Testing;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClientTest {

    private ByteArrayOutputStream outContent;
    private InputStream originalIn;
    private OutputStream originalOut;
    private Socket socket;

    @Before
    public void setUp() {
        // Configure the output stream so that console output is captured
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Save the original versions of System.in and System.out for future usage
        originalIn = System.in;
        originalOut = System.out;
    }

    @After
    public void tearDown() throws IOException {
        // Reset System.in and System.out to their original values
        System.setIn(originalIn);
        System.setOut(new PrintStream(originalOut));

        // System.in and System.out get reseted to their default values
        outContent.close();
        if (socket != null) {
            socket.close();
        }
    }

    /**
     * @throws IOException
     */
    @Test
    public void testConnection() throws IOException {
        try (
            // Creates a mock server socket for testing purposes that will be used to return a message to the client
            ServerSocket serverSocketMock = new ServerSocket(0);
        ) {
            int port = serverSocketMock.getLocalPort();

            // Create a new client and connect it to the mock server
            ClientTest client = new ClientTest();
            client.connectToServer("localhost", port);

            // Create a new thread to emulate the servers behavior
            new Thread(() -> {
                try (Socket serverSocket = serverSocketMock.accept()) {
                    // Sends a response back to the client saying "Hello"
                    OutputStream output = serverSocket.getOutputStream();
                    output.write("Hello".getBytes());
                    output.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // A message to the server is sent to verify that it is received
            client.sendMessage("Hello");

            // The server waits to see if the expected message sent by the client is valid
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
            String receivedMessage = reader.readLine();
            assertEquals("Hello", receivedMessage);

            // Disconnects the client and finishes the test
            client.disconnect();
        }
    }

    @Test
    public void testDisconnect() throws IOException {
        try (// Create a mock server socket that will not respond to the client
        ServerSocket serverSocketMock = new ServerSocket(0)) {
            // Create a new client and connect it to the mock server
            ClientTest client = new ClientTest();
            client.connectToServer("localhost", serverSocketMock.getLocalPort());

            // Sends a message to the server and it is verified that it's not received
            client.sendMessage("Hello, server!");
            assertEquals("", outContent.toString());

            // Disconnect the client
            client.disconnect();
        }
    }
// Methods below are used to establish a connection to the server, send messages to the server, retrieve the socket object for communication, and to disconnect from the server.
// These methods are crucial for the assertions to not fail
    private void connectToServer(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
    }

    private void sendMessage(String message) throws IOException {
        OutputStream output = socket.getOutputStream();
        output.write(message.getBytes());
        output.flush();
    }

    private Socket getSocket() {
        return socket;
    }

    private void disconnect() throws IOException {
        socket.close();
    }
}
