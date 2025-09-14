package csx55.overlay.node;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import csx55.overlay.transport.*;
import csx55.overlay.wireformats.*;
import java.util.concurrent.*;
import csx55.overlay.abstractions.EventWithSocket;
import csx55.overlay.util.OverlayCreator;
import csx55.overlay.abstractions.*;
import java.util.Random;

public class Registry implements Node {

    private TCPServerThread serverThread;
    private static ConcurrentMap<String, NodeInfo> nodeRegistry = new ConcurrentHashMap<>();
    private boolean overlaySetup = false;
    private volatile boolean running = true;
    private final BlockingQueue<EventWithSocket> messageQueue = new LinkedBlockingQueue<>();
    private Thread registryProcessorThread;
    private List<LinkInfo> links;

    /*
     * Main Method to start the registry
     */
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

    // ------------------------------------------------------THREAD STARTUP METHODS
    // -----------------------------------------------------------------------------------------------

    /**
     * REGISTRY THREAD - STARTS UP SERVER,INPUT,MESSAGING PROCESSES
     */

    public void start(int port) throws IOException {
        startServerThread(port);
        startMessageProcessor();
        startInputThread();
    }

    /**
     * Start the server thread to listen for incoming connections - runs
     * TCPServerThread
     */
    public void startServerThread(int port) throws IOException {
        serverThread = new TCPServerThread(port, this);
        serverThread.start();
    }

    /**
     * Thread to handle events.
     * <p>
     * Works with the blocking messageQueue. When the receiver thread adds to the
     * messageQueue, this process
     * will go in an handle that event so the reciever can continue grabbing
     * messages.
     * </p>
     */
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
                    System.err.println("Error processing event:" + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        registryProcessorThread.start();
    }

    /**
     * Thread to handle user input from console (MAIN THREAD)
     */
    private void startInputThread() {
        Scanner scn = new Scanner(System.in);

        while (running) {
            String input = scn.nextLine();

            switch (input) {
                case "list-messaging-nodes":
                    listMessagingNodes();
                    break;

                case "overlay-setup":
                    createOverlay();
                    break;

                default:
                    System.out.println("Unknown command. Available commands: list-messaging-nodes, overlay-setup");
                    break;

            }

        }

        scn.close();
    }

    // -------------------------------END OF THREAD STARTUP METHODS
    // ----------------------------------------------------------------

    // private void handleDeregistrationRequest(Deregister request, Socket socket) {
    // String nodeIP = request.getIpAddress();
    // int nodeListeningPort = request.getPortNumber();
    // String nodeKey = nodeIP + ":" + nodeListeningPort;

    // NodeInfo nodeInfo = checkForNode(nodeKey);
    // if (nodeInfo != null) {
    // nodeRegistry.remove(nodeKey);
    // System.out.println("Deregistered node: " + nodeKey);
    // } else {
    // System.out.println("Deregistration failed, node not found: " + nodeKey);
    // }
    // }

    // private NodeInfo checkForNode(String key) {
    // return nodeRegistry.get(key);
    // }

    // EVENT HANDLING
    // ------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onEvent(Event event, Socket socket) {
        try {
            EventWithSocket eventWithSocket = new EventWithSocket(event, socket);
            messageQueue.put(eventWithSocket);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Process events based on type
     * <p>
     * Run by the registryProcessorThread
     * </p>
     * 
     * @param event  Event to process
     * @param socket Socket from which event was received
     */
    private void processEvent(Event event, Socket socket) {
        switch (event.getType()) {

            case Protocol.REGISTER_REQUEST:
                handleRegistrationRequest((Register) event, socket);
                break;
            // case Protocol.DEREGISTER_REQUEST:
            // handleDeregistrationRequest((Deregister) event, socket);
            // break;
            default:
                System.out.println("Unknown event type: " + event.getType());
        }
    }

    // -----------------------------------END OF EVENT HANDLING
    // ---------------------------------------------------------------------------

    // -------------------------------------REGISTRATION
    // --------------------------------------------------------------------------------

    /**
     * Handle registration request from a node
     * 
     * @param request
     * @param socket
     */

    private void handleRegistrationRequest(Register request, Socket socket) {
        String nodeIP = request.getIpAddress();
        int nodeListeningPort = request.getPortNumber();
        String nodeKey = nodeIP + ":" + nodeListeningPort;
        String sourceIP = socket.getInetAddress().getHostAddress();
        byte status;
        String info;
        status = Protocol.FAILURE;

        // Error checking
        if (!nodeIP.equals(sourceIP)) {
            info = "IP mismatch: Requested " + nodeIP + " but connected from " + sourceIP;
        } else if (nodeRegistry.containsKey(nodeKey)) {
            info = "Node already registered: " + nodeKey;
        } else if (overlaySetup) {
            info = "Overlay already setup, cannot register new nodes";
        }

        // Sucess
        else {
            nodeRegistry.put(nodeKey, new NodeInfo(nodeIP, nodeListeningPort, socket));
            status = Protocol.SUCCESS;
            info = "Registration successful. Number of nodes: " + nodeRegistry.size();
        }

        sendRegistrationResponse(socket, status, info, nodeListeningPort);
    }

    /**
     * Send registration response to node
     * 
     * @param socket
     * @param status
     * @param info
     * @param port
     */

    private void sendRegistrationResponse(Socket socket, byte status, String info, int port) {
        try {
            Register response = new Register(status, info);

            TCPSender sender = new TCPSender();
            sender.connect(socket.getInetAddress(), port);
            sender.send(response.getBytes());

        } catch (IOException e) {
            System.err.println("Failed to send registration response: " + e.getMessage());
        }
    }

    // ------------------------------------------------END OF REGISTRATION
    // --------------------------------------------------------------------------------

    // -------------------------------------------LIST-MESSAGING-NODES
    // -------------------------------------------------------------------------

    /**
     * List all registered messaging nodes
     * <p>
     * Prints to console
     * </p>
     */
    public static void listMessagingNodes() {
        for (String key : nodeRegistry.keySet()) {
            System.out.println(key);
        }
        System.out.println("Total nodes: " + nodeRegistry.size());
    }

    // -----------------------------------------END OF LIST-MESSAGING-NODES
    // ----------------------------------------------------------------------

    // -----------------------------------------OVERLAY SETUP
    // ---------------------------------------------------------------------------------

    /**
     * Create overlay will connect each node to 4 other nodes
     * <p>
     * Uses OverlayCreator class to handle the logic of creating the overlay </p
     */
    private void createOverlay() {
        if (overlaySetup) {
            System.out.println("Overlay already set up.");
            return;
        }

        OverlayCreator creator = new OverlayCreator(nodeRegistry);
        creator.createOverlay(4); // Default of 4 connections?
        overlaySetup = true;
        sendConnectionInfoToNodes();
    }

    /**
     * Send connection info to all nodes in the registry
     * <p>
     * Uses MessagingNodesList event to send each node their connections
     * </p>
     */
    private void sendConnectionInfoToNodes() {
        try {
            for (NodeInfo node : nodeRegistry.values()) {
                MessagingNodesList message = new MessagingNodesList(node.getConnections());
                TCPSender sender = new TCPSender();
                sender.connect(node.getSocket().getInetAddress(), node.getPortNumber());
                sender.send(message.getBytes());

                System.out.println("Sent connection info to node: " + node.getKey());
            }
        } catch (Exception e) {
            System.err.println("Error sending connection info: " + e.getMessage());
        }
    }

    // ---------------------------------------END OF OVERLAY SETUP
    // --------------------------------------------------------------------------

    private void assignLinks() {
        Random r = new Random();
        for (NodeInfo node1 : nodeRegistry.values()) {
            for (String connection : node1.getConnections()) {
                NodeInfo node2 = nodeRegistry.get(connection);
                if (node2 != null && !connectionExists(node1, node2)) {
                    int linkvalue = r.nextInt(10) + 1;
                    links.add(new LinkInfo(node1, node2, linkvalue));
                }
            }
        }
    }

    /*
     * Check if connection exists
     */
    private boolean connectionExists(NodeInfo node1, NodeInfo node2) {
        for (LinkInfo link : links) {
            if (node1.equals(link.getNode1()) && node2.equals(link.getNode2())
                    || node1.equals(link.getNode2()) && node2.equals(link.getNode1())) {
                return true;
            }
        }
        return false;
    }

}