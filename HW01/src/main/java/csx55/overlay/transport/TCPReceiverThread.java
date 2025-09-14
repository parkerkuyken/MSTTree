package csx55.overlay.transport;

import java.io.IOException;
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
            while (!socket.isClosed()) {
                
                int messageLength = dataIn.readInt();
                byte[] messageData = new byte[messageLength];
                dataIn.readFully(messageData);
                processMessage(messageData);
            }
        } catch (IOException e) {
            System.out.println("Connection closed by client: " + socket.getInetAddress().getHostAddress());
        } finally {
            closeConnection();
        }
    }
    
    /**
     * Process the received message and notify the node
     * @param data (raw byte array)
     */
    private void processMessage(byte[] data) {
        try {
            EventFactory.getInstance();
            Event event = EventFactory.createEvent(data);
            
            if (node != null) {

                node.onEvent(event, socket);
            }
            
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }
    

    /**
     * Close the socket and associated streams
     */
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

    

    /**
     * @return Socket object
     */
    public Socket getSocket() {
        return socket;
    }
}