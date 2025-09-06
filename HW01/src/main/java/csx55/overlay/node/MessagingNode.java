// package csx55.overlay.node;

// import java.io.IOException;
// import java.net.InetAddress;
// import java.net.ServerSocket;
// import java.net.Socket;

// public class MessagingNode {

//     private String ipAddress;
//     private int portNumber;

//     public MessagingNode(String ipAddress, int portNumber) {
//         this.ipAddress = ipAddress;
//         this.portNumber = portNumber;
//     }

//     // Connect to the Registry
//     public void connectToRegistry(String registryIp, int registryPort) {
//         try {
//             Socket registrySocket = new Socket(registryIp, registryPort);
//             System.out.println("Connected to Registry at " + registryIp + ":" + registryPort);
//             registrySocket.close();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     public static void main(String[] args) {
//         if (args.length != 2) {
//             System.out.println("Need Target IP and Port");
//             return;
//         }

//         String registryIp = args[0];
//         int registryPort = Integer.parseInt(args[1]);

//         try {
//             InetAddress localHost = InetAddress.getLocalHost();
//             String localIp = localHost.getHostAddress();

//             ServerSocket serverSocket = new ServerSocket(0);
//             int localPort = serverSocket.getLocalPort();

//             System.out.println("Node running at " + localIp + ":" + localPort);

//             MessagingNode node = new MessagingNode(localIp, localPort);
//             node.connectToRegistry(registryIp, registryPort);

//             // Keep the server socket open for future use
//             // serverSocket.accept(); or run in a thread

//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
// }
