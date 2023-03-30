package JUnit_Testing;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import JUnit_Testing.Host.ConnectionHandler;

public class HostTesting {
    private Host host;
    private Thread hostThread;
    private Socket client1;
    private ConnectionHandler handler1;
    private Socket client2;
    private ConnectionHandler handler2;
    private Socket client3;
    private ConnectionHandler handler3;

    @Before
    public void setUp() {
        host = new Host();
        hostThread = new Thread(host);
        hostThread.start();
    }

    @After
    public void tearDown() {
        host.shutdown();
        try {
            hostThread.join();
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    /**
     * @throws IOException
     */
    @Test
    public void testConnectionHandler() throws IOException {
        Host host = new Host();
        ArrayList<ConnectionHandler> connections = new ArrayList<>();
        Socket client = new Socket("localhost", 9999);
        Host.ConnectionHandler handler = host.new ConnectionHandler(client, host.getConnections());
        Thread handlerThread = new Thread(handler);
        handlerThread.start();


        // Test ConnectionHandler with first client
        client1 = new Socket("localhost", 9999);
        handler1 = host.new ConnectionHandler(client1, connections);
        Thread handlerThread1 = new Thread(handler1);
        handlerThread1.start();

        // Test ConnectionHandler with second client
        client2 = new Socket("localhost", 9999);
        handler2 = host.new ConnectionHandler(client2, connections);
        Thread handlerThread2 = new Thread(handler2);
        handlerThread2.start();

        // Test ConnectionHandler with third client
        client3 = new Socket("localhost", 9999);
        handler3 = host.new ConnectionHandler(client3, connections);
        Thread handlerThread3 = new Thread(handler3);
        handlerThread3.start();

        // Send message from client 1 to all clients
        handler1.sendMessage("Hello, everyone!");

        // Check that all clients received message
        assertEquals("Hello, everyone!", handler2.readMessage());
        assertEquals("Hello, everyone!", handler3.readMessage());

        // Shutdown ConnectionHandlers
        handler1.shutdown();
        handler2.shutdown();
        handler3.shutdown();
        try {
            handlerThread1.join();
            handlerThread2.join();
            handlerThread3.join();
        } catch (InterruptedException e) {
            // Ignore
        }
    
    }
    
    @Test
    public void testHostBroadcast() throws IOException {
        ArrayList<ConnectionHandler> connections = new ArrayList<>();

        // Test ConnectionHandler with first client
        client1 = new Socket("localhost", 9999);
        handler1 = host.new ConnectionHandler(client1, connections);
        Thread handlerThread1 = new Thread(handler1);
        handlerThread1.start();

        // Test ConnectionHandler with second client
        client2 = new Socket("localhost", 9999);
        handler2 = host.new ConnectionHandler(client2, connections);
        Thread handlerThread2 = new Thread(handler2);
        handlerThread2.start();

        // Test ConnectionHandler with third client
        client3 = new Socket("localhost", 9999);
        handler3 = host.new ConnectionHandler(client3, connections);
        Thread handlerThread3 = new Thread(handler3);
        handlerThread3.start();

        // Broadcast message from host to all clients
        host.broadcast("Hello, everyone!");

        // Check that all clients received message
        assertEquals("Hello, everyone!", handler1.readMessage());
        assertEquals("Hello, everyone!", handler2.readMessage());
        assertEquals("Hello, everyone!", handler3.readMessage());

        // Shutdown ConnectionHandlers
        handler1.shutdown();
        handler2.shutdown();
        handler3.shutdown();
        try {
            handlerThread1.join();
            handlerThread2.join();
            handlerThread3.join();
        } catch (InterruptedException e) {

       }

    }
    }


