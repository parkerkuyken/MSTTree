package csx55.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessagingNodesList implements Event {

    private int messageType = Protocol.MESSAGING_NODES_LIST;
    private int numberOfPeers;
    private List<String> peerNodes;  // store as "ip:port"

    public MessagingNodesList(List<String> peerNodes) {
        this.numberOfPeers = peerNodes.size();  
        this.peerNodes = peerNodes;
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

        // Write each peer node as "ip:port"
        for (String nodeAddress : peerNodes) {
            byte[] nodeBytes = nodeAddress.getBytes();
            dout.writeInt(nodeBytes.length);
            dout.write(nodeBytes);
        }

        dout.flush();
        byte[] marshalledBytes = baOutputStream.toByteArray();
        dout.close();
        baOutputStream.close();

        return marshalledBytes;
    }

    private void unmarshallBytes(byte[] data) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(baInputStream);

        messageType = din.readInt();
        numberOfPeers = din.readInt();

        peerNodes = new ArrayList<>();
        for (int i = 0; i < numberOfPeers; i++) {
            int addressLength = din.readInt();
            byte[] addressBytes = new byte[addressLength];
            din.readFully(addressBytes);
            String address = new String(addressBytes);
            peerNodes.add(address);
        }

        din.close();
        baInputStream.close();
    }

    @Override
    public int getType() {
        return Protocol.MESSAGING_NODES_LIST;
    }

    public int getNumberOfPeers() { 
        return numberOfPeers; 
    }

    public List<String> getPeerNodes() { 
        return peerNodes; 
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MessagingNodesList[peers=").append(numberOfPeers).append("]:\n");
        for (String node : peerNodes) {
            sb.append("  ").append(node).append("\n");
        }
        return sb.toString();
    }
}
