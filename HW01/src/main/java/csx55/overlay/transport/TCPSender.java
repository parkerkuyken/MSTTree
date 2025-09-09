package csx55.overlay.transport;

import java.io.IOException;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.InetAddress;

public class TCPSender {
    private Socket socket;
    private DataOutputStream dataOut;
    
    public TCPSender() {
    }
    
    // Constructor for existing socket (for responses)
    public TCPSender(Socket socket) throws IOException {
        this.socket = socket;
        this.dataOut = new DataOutputStream(socket.getOutputStream());
    }
    
    public void connect(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.dataOut = new DataOutputStream(socket.getOutputStream());
        System.out.println("Connected to " + host + ":" + port);
    }
    
    // Connect using InetAddress
    public void connect(InetAddress address, int port) throws IOException {
        this.socket = new Socket(address, port);
        this.dataOut = new DataOutputStream(socket.getOutputStream());
        System.out.println("Connected to " + address.getHostAddress() + ":" + port);
    }
    
    // Send data over the connection
    public void send(byte[] data) throws IOException {
        if (dataOut == null) {
            throw new IOException("Not connected to any host");
        }
        
        if (data == null || data.length == 0) {
            throw new IOException("Cannot send empty data");
        }
        
        // Protocol: write length first, then data
        dataOut.writeInt(data.length);
        dataOut.write(data);
        dataOut.flush();
        
        System.out.println("Sent " + data.length + " bytes to " + 
            socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
    }
    
    // Send data with explicit destination
    public void sendTo(String host, int port, byte[] data) throws IOException {
        // Create temporary connection for one-time send
        try (Socket tempSocket = new Socket(host, port);
             DataOutputStream tempOut = new DataOutputStream(tempSocket.getOutputStream())) {
            
            tempOut.writeInt(data.length);
            tempOut.write(data);
            tempOut.flush();
            
            System.out.println("Sent " + data.length + " bytes to " + host + ":" + port);
        }
    }
    
    // Check connection status
    public boolean isConnected() {
        return socket != null && !socket.isClosed() && dataOut != null;
    }
    
    // Get the underlying socket
    public Socket getSocket() {
        return socket;
    }
    
    // Close the connection
    public void close() {
        try {
            if (dataOut != null) {
                dataOut.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing TCPSender: " + e.getMessage());
        }
    }
    
    // Get remote address info
    public String getRemoteInfo() {
        if (socket != null && !socket.isClosed()) {
            return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        }
        return "Not connected";
    }
}