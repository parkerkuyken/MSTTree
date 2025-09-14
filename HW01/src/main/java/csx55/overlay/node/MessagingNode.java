package csx55.overlay.node;

import java.util.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import csx55.overlay.transport.*;
import csx55.overlay.wireformats.*;


public class MessagingNode implements Node {

    private TCPServerThread serverThread;
    private TCPSender registrySender;
    private String ipAddress;
    private int listeningPort;
    private boolean registered = false;
    private String registryHost;
    private int registryPort;

    // List of connections to other messaging nodes
    private List<String> connections = new ArrayList<>(); 
    
  
    private final BlockingQueue<Event> messageQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;
    private Thread messageProcessorThread;



    
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("java csx55.overlay.node.MessagingNode registry-host registry-port");
            return;
        }
        
        try {
            String registryHost = args[0];
            int registryPort = Integer.parseInt(args[1]);
            
            MessagingNode node = new MessagingNode();
            
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



    //------------------------------Thread haldling ---------------------------------------------------------------------------------
   
    
    /**
     * REGISTRY THREAD - STARTS UP SERVER,INPUT,MESSAGING PROCESSES 
     */
    public void start(String registryHost, int registryPort) {
        this.registryHost = registryHost;
        this.registryPort = registryPort;
        
        try {
            ipAddress = getLocalIP();
            
            startServerThread();
            startMessageProcessor();
            registerWithRegistry();
            
            
        } catch (Exception e) {
            System.err.println("Failed to start node: " + e.getMessage());
            e.printStackTrace();
        }
    }

     /**
     * Start the server thread to listen for incoming connections - runs TCPServerThread
     */
    private void startServerThread() throws IOException {
        serverThread = new TCPServerThread(0, this);
        serverThread.start();
        listeningPort = serverThread.getLocalPort();
        System.out.println("Node listening on port: " + listeningPort);
    }



    /**
     * Thread to handle events.
     * <p>Works with the blocking messageQueue. When the receiver thread adds to the messageQueue, this process
     * will go in an handle that event so the reciever can continue grabbing messages.</p>
     */
    private void startMessageProcessor() {
        messageProcessorThread = new Thread(() -> {
            while (running) {
                try {
                    Event event = messageQueue.take();
                    processEvent(event);
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        messageProcessorThread.start();
    }


    /**
     * Stops all threads and closes connections
     */
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
    
    //---------------------------------------------END of Thread handling ------------------------------------------------------------------



    //----------------------------------------------EVENT handling ----------------------------------------------------------------------------
    
    @Override
    public void onEvent(Event event, Socket socket) {
        try {
            messageQueue.put(event);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
   
    
    /**
     * Process events based on type
     * <p> Run by the registryProcessorThread </p>
     * @param event Event to process
     * @param socket Socket from which event was received   
     */
    private synchronized void processEvent(Event event) {

        switch (event.getType()) {

            case Protocol.REGISTER_RESPONSE:
                handleRegistrationResponse((Register) event);
                break;

            case Protocol.MESSAGING_NODES_LIST:
                handleMessagingNodesListResponse((MessagingNodesList) event);
                break;
                
            default:
                System.out.println("Unexpected event type: " + event.getType());
        }
    }
    
    
    //------------------------------------------END OF Event handling -----------------------------------------------------------------------------
    



    //--------------------------------------------GETTERS ------------------------------------------------------------------------------------
    
    /**
     * @return String Local IP address 
     * @throws UnknownHostException when IP cannot be determined
     */
    private synchronized String getLocalIP() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }
    
    /**
     * @return True if registered with registry, false otherwise
     */
    public synchronized boolean isRegistered() {
        return registered;
    }
    
    /**
     * @return String ip:port
     */
    public synchronized String getNodeInfo() {
        return ipAddress + ":" + listeningPort;
    }
    
    /**
     * @return int messageQueue size
     */
    public synchronized int getQueueSize() {
        return messageQueue.size();
    }

    //------------------------------------------END OF GETTERS -------------------------------------------------------------------------------
    


    //------------------------------------------REGISTRATION --------------------------------------------------------------------------------

    /**
     * Handles the registration response from the registry
     * @param response Register response event
     */
    private synchronized void handleRegistrationResponse(Register response) {
        byte status = response.getStatusCode();
        String additionalInfo = response.getAdditionalInfo();
        
        if (status == Protocol.SUCCESS) {
            registered = true;
            System.out.println("Node REGISTERED ");
        } else {
            System.err.println("REGISTRATION FAILED: " + additionalInfo);
            registered = false;
            stop();
        }
    }

    /**
     * Sends registration request to the registry
     * @throws IOException if connection or sending fails
     */
    private synchronized void registerWithRegistry() {
        try {
            registrySender = new TCPSender();
            registrySender.connect(registryHost, registryPort);
            
            Register registerRequest = new Register(ipAddress, listeningPort);
            byte[] messageData = registerRequest.getBytes();
            registrySender.send(messageData);
            
        } catch (IOException e) {
            System.err.println("Registration failed: " + e.getMessage());
            e.printStackTrace();
            stop();
        }
    }
    
    //-----------------------------------------END OF REGISTRATION ----------------------------------------------------------------------




    //-----------------------------------MESSAGING NODES LIST HANDLING ----------------------------------------------------------------

    /**
     * Handles the messaging nodes list response from the registry
     * <p> Updates the connections list post overlay creation </p>
     * @param response MessagingNodesList event
     */
    private synchronized void handleMessagingNodesListResponse(MessagingNodesList response) {
        connections = response.getPeerNodes();
        for (String node : connections) {
            System.out.println("Node: " + node);
        }
        
    
        }
}