// package csx55.overlay.node;

// import java.io.IOException;
// import java.net.ServerSocket;
// import java.net.Socket;
// import java.util.HashMap;
// // Add this import at the top of Registry.java
// import java.util.ArrayList;

// public class Registry {

//     private ArrayList<Socket> messagingNodes; 
//     private ServerSocket serverSocket;

//     public Registry(int port) throws IOException {
//         messagingNodes = new ArrayList<>();
//         serverSocket = new ServerSocket(port);
//         System.out.println("Registry listening on port " + port);
//     }

//     public void start() {
//         try {
//             while (true) {
//                 Socket messagenode = serverSocket.accept();
//                 String clientIp = messagenode.getInetAddress().getHostAddress();
//                 int clientPort = messagenode.getPort();

                

//                 System.out.println("Node connected: " + clientIp + ":" + clientPort);

//                 //register(clientIp, clientPort);
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     public void unregister(String clientIP, int clientPort){
//         messagingNodes.remove(clientIP);
//     }

//     public void register(Socket messagenode){
//         messagingNodes.put(messagenode);

//     }
//     // public void register(String clientIp, int clientPort) {
//     //     messagingNodes.put(clientIp, clientPort);
//     //     System.out.println("Registered: " + clientIp + ":" + clientPort);
//     // }

//     public static void main(String[] args) {
//         int port = Integer.parseInt(args[0]);
//         try {
//             Registry registry = new Registry(port);
//             registry.start();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
// }
