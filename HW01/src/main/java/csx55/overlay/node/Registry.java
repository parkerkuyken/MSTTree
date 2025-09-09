package csx55.overlay.node;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import csx55.overlay.transport.TCPSender;
import csx55.overlay.transport.TCPServerThread;
import csx55.overlay.wireformats.*;

public class Registry implements Node {
    private TCPServerThread serverThread;
    private ConcurrentMap<String, NodeInfo> nodeRegistry = new ConcurrentHashMap<>();
    private boolean overlaySetup = false;

    
    private Thread registryProcessorThread;
    private volatile boolean running = true;
    private final BlockingQueue<EventWithSocket> messageQueue = new LinkedBlockingQueue<>();

    private class EventWithSocket {
        public Event event;
        public Socket socket;
        
        public EventWithSocket(Event event, Socket socket) {
            this.event = event;
            this.socket = socket;
        }
    }

    public class NodeInfo {
        private String ipAddress;
        private int portNumber;
        private Socket socket;
        
        public NodeInfo(String ipAddress, int portNumber, Socket socket) {
            this.ipAddress = ipAddress;
            this.portNumber = portNumber;
            this.socket = socket;
        }
        
        public String getKey() {
            return ipAddress + ":" + portNumber;
        }
        
        public String getIpAddress() { return ipAddress; }
        public int getPortNumber() { return portNumber; }
        public Socket getSocket() { return socket; }
    }

    public void start(int port) throws IOException {
        System.out.println("This is it");
        serverThread = new TCPServerThread(port, this);
        serverThread.start();
        System.out.println("Registry started on port: " + port);
        startMessageProcessor();
       
        while (running) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java csx55.overlay.node.Registry <port>");
            System.exit(1);
        }
    
        try {
            int port = Integer.parseInt(args[0]);
            Registry registry = new Registry();
            registry.start(port);
        } catch (Exception e) {
            System.err.println("Registry startup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processEvent(Event event, Socket socket) {
        switch (event.getType()) {
            case Protocol.REGISTER_REQUEST:
                handleRegistrationRequest((Register) event, socket);
                break;
            default:
                System.out.println("Unknown event type: " + event.getType());
        }
    }

    private NodeInfo checkForNode(String key) {
        return nodeRegistry.get(key);
    }
    
    private void handleRegistrationRequest(Register request, Socket socket) {
        String nodeIP = request.getIpAddress();
        int nodeListeningPort = request.getPortNumber(); 
        String nodeKey = nodeIP + ":" + nodeListeningPort;
        String sourceIP = socket.getInetAddress().getHostAddress();
        int sourcePort = socket.getPort();
    
        System.out.println("Registration request: " + sourceIP + ":" + sourcePort);
        System.out.println("isten on: " + nodeListeningPort);
        System.out.println("Connection socket: " + socket.getInetAddress() + ":" + socket.getPort());
    
        byte status;
        String info;
    

        if (!nodeIP.equals(sourceIP)) {
            status = Protocol.FAILURE;
            info = "IP mismatch: Requested " + nodeIP + " but connected from " + sourceIP;
            System.err.println("Registration failed: " + info);
        } 

        else if (nodeRegistry.containsKey(nodeKey)) {
            status = Protocol.FAILURE;
            info = "Node already registered: " + nodeKey;
            System.err.println("Registration failed: " + info);
        }
        else if (overlaySetup) {
            status = Protocol.FAILURE;
            info = "Overlay already setup, cannot register new nodes";
            System.err.println("Registration failed: " + info);
        }

        else {
            nodeRegistry.put(nodeKey, new NodeInfo(nodeIP, nodeListeningPort, socket));
            status = Protocol.SUCCESS;
            info = "Registration successful. Number of nodes: " + nodeRegistry.size();
            System.out.println("Registered node: " + nodeKey + " (connected via port " + sourcePort + ")");
        }
    
        sendRegistrationResponse(socket, status, info, nodeListeningPort);
    }

    private void sendRegistrationResponse(Socket socket, byte status, String info, int port) {
        try {
            Register response = new Register(status, info);
            
            TCPSender sender = new TCPSender();
            sender.connect(socket.getInetAddress(), port);
            sender.send(response.getBytes());
            
            System.out.println("Sent registration response to: " + 
                socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
            
        } catch (IOException e) {
            System.err.println("Failed to send registration response: " + e.getMessage());
        }
    }

    private void startMessageProcessor() {
        registryProcessorThread = new Thread(() -> {
            while (running) {
                try {
                    EventWithSocket eventWithSocket = messageQueue.take();
                    processEvent(eventWithSocket.event, eventWithSocket.socket);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error processing event: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        registryProcessorThread.start();
    }
    
    @Override
    public void onEvent(Event event, Socket socket) {
        try {
            EventWithSocket eventWithSocket = new EventWithSocket(event, socket);
            messageQueue.put(eventWithSocket);
            
            System.out.println("Received event type: " + event.getType() + " from: " + 
                socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void listMessagingNodes() {
        System.out.println("Registered messaging nodes:");
        for (String key : nodeRegistry.keySet()) {
            System.out.println(key);
        }
        System.out.println("Total nodes: " + nodeRegistry.size());
    }
}