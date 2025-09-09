package csx55.overlay.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import csx55.overlay.transport.TCPSender;
import csx55.overlay.transport.TCPServerThread;
import csx55.overlay.wireformats.Event;
import csx55.overlay.wireformats.EventFactory;
import csx55.overlay.wireformats.Protocol;
import csx55.overlay.wireformats.Register;

public class MessagingNode implements Node {

    private TCPServerThread serverThread;
    private TCPSender registrySender;
    private String ipAddress;
    private int listeningPort;
    private boolean registered = false;
    private String registryHost;
    private int registryPort;
    
    // Message processing queue and control variables
    private final BlockingQueue<Event> messageQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;
    private Thread messageProcessorThread;
    
    @Override
    public void onEvent(Event event, Socket socket) {
        System.out.println("Received event type: " + event.getType() + " from socket: " + 
            socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
        
        try {
            messageQueue.put(event);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Failed to queue event: " + e.getMessage());
        }
    }
    
    public void start(String registryHost, int registryPort) {
        this.registryHost = registryHost;
        this.registryPort = registryPort;
        
        try {
            ipAddress = getLocalIP();
            System.out.println("Node IP address: " + ipAddress);
            
            startServerThread();
            startMessageProcessor();
            registerWithRegistry();
            
            System.out.println("Waiting for registration response...");
            
            //Change this to a while true loop in which accepts an input 
            waitForRegistration(30000);
            
        } catch (Exception e) {
            System.err.println("Failed to start node: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void waitForRegistration(long timeoutMs) {
        long startTime = System.currentTimeMillis();
        
        while (running && !registered && (System.currentTimeMillis() - startTime) < timeoutMs) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        if (!registered) {
            System.err.println("Registration timed out after " + timeoutMs + "ms");
            stop();
        }
    }
    
    private void startServerThread() throws IOException {
        serverThread = new TCPServerThread(0, this);
        serverThread.start();
        listeningPort = serverThread.getLocalPort();
        System.out.println("Node listening on port: " + listeningPort);
    }
    
    private void startMessageProcessor() {
        messageProcessorThread = new Thread(() -> {
            System.out.println("Message processor thread started");
            
            while (running) {
                try {
                    Event event = messageQueue.take();
                    System.out.println("Processing event type: " + event.getType() + " from queue");
                    
                    processEvent(event);
                    
                } catch (InterruptedException e) {
                    System.out.println("Message processor interrupted");
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error processing event: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println("Message processor thread stopped");
        });
        
        messageProcessorThread.setDaemon(true);
        messageProcessorThread.start();
    }
    
    private void processEvent(Event event) {
        switch (event.getType()) {
            case Protocol.REGISTER_RESPONSE:
                System.out.println("Processing REGISTER_RESPONSE");
                handleRegistrationResponse((Register) event);
                break;
                
            
            default:
                System.out.println("Unexpected event type in processor: " + event.getType());
        }
    }
    
    private void registerWithRegistry() {
        try {
            registrySender = new TCPSender();
            registrySender.connect(registryHost, registryPort);
            System.out.println("Connected to registry at " + registryHost + ":" + registryPort);
            
            Register registerRequest = new Register(ipAddress, listeningPort);
            byte[] messageData = registerRequest.getBytes();
            registrySender.send(messageData);
            
            System.out.println("Registration request sent to registry");
            
        } catch (IOException e) {
            System.err.println("Registration failed: " + e.getMessage());
            e.printStackTrace();
            stop();
        }
    }
    
    private void handleRegistrationResponse(Register response) {
        byte status = response.getStatusCode();
        String additionalInfo = response.getAdditionalInfo();
        
        System.out.println("Status: " + status + ", Info: " + additionalInfo);
        
        if (status == Protocol.SUCCESS) {
            registered = true;
            System.out.println("WOOOOOOO WHOOOT IT WORKED PARK");
        } else {
            System.err.println("REGISTRATION FAILED: " + additionalInfo);
            registered = false;
            stop();
        }
    }
    
    public void stop() {
        running = false;
        if (messageProcessorThread != null) {
            messageProcessorThread.interrupt();
        }
        if (serverThread != null) {
            serverThread.stopServer();
        }
        if (registrySender != null) {
            registrySender.close();
        }
        System.out.println("Messaging node stopped");
    }
    
    private String getLocalIP() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }
    
    public boolean isRegistered() {
        return registered;
    }
    
    public String getNodeInfo() {
        return ipAddress + ":" + listeningPort;
    }
    
    public int getQueueSize() {
        return messageQueue.size();
    }
    
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java csx55.overlay.node.MessagingNode registry-host registry-port");
            return;
        }
        
        try {
            System.out.println("null");
            String registryHost = args[0];
            int registryPort = Integer.parseInt(args[1]);
            
            MessagingNode node = new MessagingNode();
            System.out.println("Starting messaging node...");
            System.out.println("Connecting to registry: " + registryHost + ":" + registryPort);
            
            node.start(registryHost, registryPort);
            
            if (node.isRegistered()) {
                Thread.sleep(Long.MAX_VALUE);
            } else {
                System.out.println("Node failed to register. Exiting.");
            }
            
        } catch (Exception e) {
            System.err.println("Node startup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}