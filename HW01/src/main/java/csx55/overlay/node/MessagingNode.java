package csx55.overlay.node;

public class MessagingNode {
    private final String ip;
    private final int port;

    // Primary constructor - explicit IP and port
    public MessagingNode(String ip, int port) {
        if (ip == null || ip.trim().isEmpty()) {
            throw new IllegalArgumentException("IP cannot be null or empty");
        }
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 0 and 65535");
        }
        this.ip = ip.trim();
        this.port = port;
    }

    // Secondary constructor - from "ip:port" string
    public MessagingNode(String ipPortString) {
        if (ipPortString == null || !ipPortString.contains(":")) {
            throw new IllegalArgumentException("Invalid IP:port format: " + ipPortString);
        }
        
        String[] parts = ipPortString.split(":", 2); // Split into max 2 parts
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid IP:port format: " + ipPortString);
        }
        
        this.ip = parts[0].trim();
        try {
            this.port = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port number: " + parts[1], e);
        }
        
        // Validate after parsing
        if (this.ip.isEmpty()) {
            throw new IllegalArgumentException("IP cannot be empty");
        }
        if (this.port < 0 || this.port > 65535) {
            throw new IllegalArgumentException("Port must be between 0 and 65535");
        }
    }

    // Renamed to follow Java naming conventions (camelCase)
    public String serialize() {
        return ip + ":" + port;
    }
    
    public String getIp() { return ip; }
    public int getPort() { return port; }
    
    @Override
    public String toString() {
        return serialize(); // Use the same format
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MessagingNode)) return false;
        MessagingNode other = (MessagingNode) obj;
        return ip.equals(other.ip) && port == other.port;
    }
    
    @Override
    public int hashCode() {
        return ip.hashCode() * 31 + port;
    }

    // Optional: Add some helper methods
    public boolean isLocalhost() {
        return "localhost".equals(ip) || "127.0.0.1".equals(ip) || "::1".equals(ip);
    }
    
    public boolean isValid() {
        return ip != null && !ip.isEmpty() && port >= 0 && port <= 65535;
    }
}