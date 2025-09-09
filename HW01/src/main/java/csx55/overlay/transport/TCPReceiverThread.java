package csx55.overlay.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.net.Socket;
import csx55.overlay.node.Node;
import csx55.overlay.wireformats.Event;
import csx55.overlay.wireformats.EventFactory;

public class TCPReceiverThread extends Thread {
    private Socket socket;
    private Node node;
    private DataInputStream dataIn;
    
    public TCPReceiverThread(Socket socket, Node node) throws IOException {
        this.socket = socket;
        this.node = node;
        this.dataIn = new DataInputStream(socket.getInputStream());
    }
    
    @Override
    public void run() {
        try {
            System.out.println("TCPReceiverThread started for: " + 
                socket.getInetAddress().getHostAddress());
            
            while (!socket.isClosed()) {
                
                int messageLength = dataIn.readInt();
                
                // Read the actual message data
                byte[] messageData = new byte[messageLength];
                dataIn.readFully(messageData);
                System.out.println("Hello got a message over here");
                processMessage(messageData);
            }
            
        } catch (IOException e) {
            System.out.println("Connection closed by client: " + 
                socket.getInetAddress().getHostAddress() + " - " + e.getMessage());
        } finally {
            closeConnection();
        }
    }
    
    private void processMessage(byte[] data) {
        try {
            Event event = EventFactory.getInstance().createEvent(data);
            System.out.println("Processing message");
            if (node != null) {
                // CRITICAL: You must pass the socket to onEvent!
                node.onEvent(event, socket); // ← ADD THIS SOCKET PARAMETER
            }
            
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }
    
    public Socket getSocket() {
        return socket;
    }
    
    private void closeConnection() {
        try {
            if (dataIn != null) {
                dataIn.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}