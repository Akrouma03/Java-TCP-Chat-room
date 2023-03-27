import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Defines Host class
public class Host implements Runnable {

    // Define instance variables
    private ArrayList<ConnectionHandler> connections; // ArrayList that holds the connection handlers for each client.
    private ServerSocket server; // ServerSocket to listen for incoming connections
    private boolean done; // Boolean flag to indicate if the server is required to shutdown
    private ExecutorService pool; // Thread pool to manage multiple client connections

    // Host constructor
    public Host() {
        connections = new ArrayList<>();
        done = false;
    }

    // Host run method - looks for incoming connections and starts new
    // ConnectionHandler threads for each client
    @Override
    public void run() {
        try {
            server = new ServerSocket(9999);
            pool = Executors.newCachedThreadPool(); // Initialize thread pool
            while (!done) {
                Socket client = server.accept(); // Wait for incoming connections
                ConnectionHandler handler = new ConnectionHandler(client, connections); // Creates a new ConnectionHandler for the client

                connections.add(handler); // Adds the ConnectionHandler to the ArrayList
                pool.execute(handler); // Starts new thread for the ConnectionHandler
            }
        } catch (Exception e) {
            shutdown(); // If an exception occurs, shutdown the server
        }
    }

    // Method to broadcast a message to all connected clients
    public void broadcast(String message) {
        for (ConnectionHandler ch : connections) { // Loop through all ConnectionHandlers in ArrayList
            if (ch != null) {
                ch.sendMessage(message); // If ConnecionHandler != null , send message to client
            }
        }
    }

    // Method to shutdown the server
    public void shutdown() {
        try {
            done = true; // Sets done flag to true
            pool.shutdown(); // Shuts down the thread pool
            if (!server.isClosed()) {
                server.close(); // If server is not already closed, close the server socket
            }
            for (ConnectionHandler ch : connections) { // Loop through all ConnectionHandlers in ArrayList
                ch.shutdown(); // Shutdown each ConnectionHandler
            }
        } catch (IOException e) {
            // Ignore any exceptions that occur
        }
    }

    // Define ConnectionHandler class
    class ConnectionHandler implements Runnable {

        // Define instance variables
        private Socket client; // Socket for client connection
        private BufferedReader in; // BufferedReader to read input from client
        private PrintWriter out; // PrintWriter to send output to client
        private String ID; // String to store clients ID
        private ArrayList<ConnectionHandler> connections;
        private boolean isFirstUser = false;

        // ConnectionHandler constructor
        public ConnectionHandler(Socket client, ArrayList<ConnectionHandler> connections) {
            this.client = client;
            this.connections = connections;
        }

        // ConnectionHandler run method - reads input from client and sends message to all the clients that are connected
        @Override
        public void run() {
            try {
                out = new PrintWriter(client.getOutputStream(), true); // Initializes PrintWriter to send output to the client
                                                                       
                in = new BufferedReader(new InputStreamReader(client.getInputStream())); // Initializes BufferedReader to read input from the client


                out.println("Please Enter Your ID: "); // Asks the client to enter their ID
                ID = in.readLine(); // Reads the clients ID
                System.out.println(ID + " Connected!"); // Prints confirmation of the clients ID and that they are now connceted
                broadcast(ID + " Joined the chat!"); // Sends a message to all clients that are connected that a new user has joined

                // Give the first user to connect a welcome notification
                if (connections.size() == 1) {
                    isFirstUser = true;
                    out.println("Welcome, you are the first user to connect to the chat.");
                }

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/changeID")) { // Command for the user to change ID
                        String[] messageSplit = message.split(" ", 2);
                        if (messageSplit.length == 2) { // Check to see if new ID is provided in correct format
                            broadcast(ID + " renamed themselves to " + messageSplit[1]); // Sends a message to all connected clients that a client has changed their ID

                            System.out.println(ID + " renamed themselves to " + messageSplit[1]); // Prints the clients changed ID to the console

                            ID = messageSplit[1];
                            out.println("Successfully changed ID to " + ID); // Sends a message to the client that their ID change was successful

                        } else {
                            out.println("No ID Provided!"); // If ID is invalid / No ID was provided sends an error message to the client

                        }
                        // Private message command
                    } else if (message.startsWith("/pm")) {
                        String[] messageSplit = message.split(" ", 2); // Splits the message into two parts, the recipient and the message

                        if (messageSplit.length == 2) { // Check to see if == 2
                            String[] privateMessageSplit = messageSplit[1].split(" ", 2); // Splits the private message into two parts, recipient and message

                            if (privateMessageSplit.length == 2) { // Check to see if == 2
                                // Gets the recipient and message from the split
                                String recipient = privateMessageSplit[0];
                                String pmMessage = privateMessageSplit[1];
                                boolean recipientFound = false;
                                // Iterates through connected clients to find the recipient
                                for (Host.ConnectionHandler ch : connections) {
                                    if (ch.ID.equals(recipient)) { // If recipient is found, the private message will send and then end the loop

                                        ch.sendMessage(ID + " (private message): " + pmMessage);
                                        out.println("Sent private message to " + recipient + ": " + pmMessage);
                                        recipientFound = true;
                                        break;
                                    }
                                }
                                if (!recipientFound) { // If recipient is not found, an error message will be printed
                                    out.println(recipient + " not found");
                                }
                            } else {
                                out.println("Invalid private message format"); // Error message for invalid format
                            }
                        }
                    }
                    // Show user info using "/info"
                    else if (message.startsWith("/info")) {
                        out.println(ID + client);
                    }

                    else if (message.startsWith("/quit")) { // Commands to quit the chat
                        broadcast(ID + " left the chat!"); // Outputs a message to all connected clients that a user has quit the chat

                        shutdown(); // Closes the connection for the client that quit
                    } else {
                        broadcast(ID + ": " + message); // Outputs a message to all the clients
                    }

                }
            } catch (IOException e) {
                shutdown();
            }
        }

        public void sendMessage(String message) { // Method to send messages to clients
            out.println(message); // Outputs message to the client
        }

        public void shutdown() { // Method to close connection
            try {
                in.close(); // Closes the BufferedReader used to read input from the client
                out.close(); // Closes the PrintWriter used to send output from the client
                if (!client.isClosed()) { // Check to see if clients socket is still open
                    client.close(); // Closes the clients connection
                }
            } catch (IOException e) {
            }
        }
    }

    public static void main(String[] args) {
        Host server = new Host(); // Creates a new instance of the Host Class
        server.run(); // Starts the server
    }
}