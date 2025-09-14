package csx55.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import csx55.overlay.node.Node; 

public class TCPServerThread extends Thread {
    private ServerSocket serverSocket;
    private Node node; 
    private boolean running;
    
    public TCPServerThread(int port, Node node) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.node = node;
        this.running = true;
    }
    
    
    @Override
    public void run() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                
                //START RECEIVER THREAD FOR EACH CONNECTION
                TCPReceiverThread receiverThread = new TCPReceiverThread(clientSocket, node);
                receiverThread.start();
                
            } catch (IOException e) {
                if (running) {
                    System.err.println("TCPServerThread accept error: " + e.getMessage());
                }
            }
        }
    }
    

    /**
     * @return int Port number
     */
    public int getLocalPort() {
        return serverSocket.getLocalPort();
    }
    
    /**
     * Stop the server thread and close the server socket
     */
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