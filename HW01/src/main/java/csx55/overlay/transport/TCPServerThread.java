package csx55.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import csx55.overlay.node.Node; // For registry or messaging node

public class TCPServerThread extends Thread {
    private ServerSocket serverSocket;
    private Node node; // Can be Registry or MessagingNode
    private boolean running;
    
    public TCPServerThread(int port, Node node) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.node = node;
        this.running = true;
    }
    
    // Constructor for messaging nodes (auto-assign port)
    public TCPServerThread(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.running = true;
        this.node = null; // Messaging nodes might not need node reference
    }
    
    @Override
    public void run() {
        System.out.println("TCPServerThread listening on port: " + serverSocket.getLocalPort());
        
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection from: " + 
                    clientSocket.getInetAddress().getHostAddress());
                
                TCPReceiverThread receiverThread = new TCPReceiverThread(clientSocket, node);
                receiverThread.start();
                
            } catch (IOException e) {
                if (running) {
                    System.err.println("TCPServerThread accept error: " + e.getMessage());
                }
            }
        }
    }
    
    public int getLocalPort() {
        return serverSocket.getLocalPort();
    }
    
    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }
}