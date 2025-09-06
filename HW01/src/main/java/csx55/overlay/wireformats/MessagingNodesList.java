package csx55.overlay.wireformats;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessagingNodesList implements Event {

    private int messageType = Protocol.MESSAGING_NODES_LIST;
    private int numberOfPeers;
    private List<NodeInfo> peerNodes;

    public MessagingNodesList(int numberOfPeers, List<NodeInfo> peerNodes) {
        this.numberOfPeers = numberOfPeers;
        this.peerNodes = peerNodes;
    }

    public int getType(){
        return Protocol.MESSAGING_NODES_LIST;
    }
    public MessagingNodesList(byte[] data) throws IOException {
        unmarshallBytes(data);
    }

    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(baOutputStream);

        dout.writeInt(messageType);
        dout.writeInt(numberOfPeers);

        // Write each peer node info
        for (NodeInfo node : peerNodes) {
            byte[] ipBytes = node.getIp().getBytes();
            dout.writeInt(ipBytes.length);
            dout.write(ipBytes);
            dout.writeInt(node.getPort());
        }

        dout.flush();
        byte[] marshalledBytes = baOutputStream.toByteArray();
        dout.close();
        baOutputStream.close();

        return marshalledBytes;
    }

    private void unmarshallBytes(byte[] data) throws IOException {
        java.io.ByteArrayInputStream baInputStream = new java.io.ByteArrayInputStream(data);
        java.io.DataInputStream din = new java.io.DataInputStream(baInputStream);

        messageType = din.readInt();
        numberOfPeers = din.readInt();

        peerNodes = new ArrayList<>();
        for (int i = 0; i < numberOfPeers; i++) {
            int ipLength = din.readInt();
            byte[] ipBytes = new byte[ipLength];
            din.readFully(ipBytes);
            String ip = new String(ipBytes);
            int port = din.readInt();
            peerNodes.add(new NodeInfo(ip, port));
        }

        din.close();
        baInputStream.close();
    }

    // Getters
    public int getMessageType() { return messageType; }
    public int getNumberOfPeers() { return numberOfPeers; }
    public List<NodeInfo> getPeerNodes() { return peerNodes; }

    @Override
    public String toString() {
        return "MessagingNodesList - Peers: " + numberOfPeers;
    }

    // Inner class to represent node information
    public static class NodeInfo {
        private String ip;
        private int port;

        public NodeInfo(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        public String getIp() { return ip; }
        public int getPort() { return port; }

    }
}