package csx55.overlay.abstractions;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NodeInfo{
    private final String ipAddress;
    private final int portNumber;
    private final Socket socket;
    private final List<String> connections;  // neighbors as "ip:port"
    
    public NodeInfo(String ipAddress, int portNumber, Socket socket) {
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        this.socket = socket;
        this.connections = new ArrayList<>();
    }
    
    /**
     * @return String "ip:port"
     */
    public synchronized String getKey() {
        return ipAddress + ":" + portNumber;
    }

    /**
     * @return int number of connections
     */
    public synchronized int getNumConnections() {
        return connections.size();
    }


    /**
     * Add a connection to the list of connections if it doesn't already exist.
     * @param String address
     */
    public synchronized void addConnection(String address) {
        if (!connections.contains(address)) {  // avoid duplicate counting
            connections.add(address);
        }
    }


    /**
     * Remove a connection from the list of connections.
     * @param String address
     */
     
    public synchronized void removeConnection(String address) {
        connections.remove(address);
    }

    /**
     * @return String ipAddress
     */
    public synchronized String getIpAddress() { 
        return ipAddress; 
    }
    
    /**
     * @return int portNumber
     */
    public synchronized int getPortNumber() { 
        return portNumber; 
    }
    
    /**
     * @return Socket socket
     */
    public synchronized Socket getSocket() { 
        return socket; 
    }
    
    /**
     * @return List(String) connections
     */
    public synchronized List<String> getConnections() {
        return connections;
    }

}
