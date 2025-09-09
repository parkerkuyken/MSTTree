package csx55.overlay.node;
import java.net.Socket;

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
