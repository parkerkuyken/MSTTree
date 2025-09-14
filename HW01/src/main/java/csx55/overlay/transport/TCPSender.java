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
    

    /**
     * Connect using hostname and port
     * @param host
     * @param port
     * @throws IOException
     */
    public void connect(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.dataOut = new DataOutputStream(socket.getOutputStream());
        // System.out.println("Connected to " + host + ":" + port);
    }
    

    /**
     * Connect using InetAddress and port
     * @param address
     * @param port
     * @throws IOException
     */
    public void connect(InetAddress address, int port) throws IOException {
        this.socket = new Socket(address, port);
        this.dataOut = new DataOutputStream(socket.getOutputStream());
        // System.out.println("Connected to " + address.getHostAddress() + ":" + port);
    }
    

    /**
     * Send data over the connected socket
     * @param data
     * @throws IOException
     */
    public void send(byte[] data) throws IOException {
        if (dataOut == null) {
            throw new IOException("Not connected to any host");
        }
        
        if (data == null || data.length == 0) {
            throw new IOException("Cannot send empty data");
        }
        
        dataOut.writeInt(data.length);
        dataOut.write(data);
        dataOut.flush();
        
        // System.out.println("Sent " + data.length + " bytes to " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
    }
    

    /**
     * One-time send to specified host and port without maintaining connection
     * @param host
     * @param port
     * @param data
     * @throws IOException
     */
    public void sendTo(String host, int port, byte[] data) throws IOException {
        try (Socket tempSocket = new Socket(host, port);
             DataOutputStream tempOut = new DataOutputStream(tempSocket.getOutputStream())) {
            
            tempOut.writeInt(data.length);
            tempOut.write(data);
            tempOut.flush();
            
            // System.out.println("Sent " + data.length + " bytes to " + host + ":" + port);
        }
    }


   /**
    * Check if connected
    * @return true if connected, false otherwise
    */
    public boolean isConnected() {
        return socket != null && !socket.isClosed() && dataOut != null;
    }

    
    /**
     * Get the underlying socket
     * @return Socket object
     */
    public Socket getSocket() {
        return socket;
    }


      /**
     * Get remote address info as "ip:port"
     * @return String representation of remote address
     */
    public String getRemoteInfo() {
        if (socket != null && !socket.isClosed()) {
            return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        }
        return "Not connected";
    }

    
    /**
     * Close the connection and cleanup resources
     * @throws IOException
     */
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
}

  