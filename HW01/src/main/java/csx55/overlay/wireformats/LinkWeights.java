package csx55.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LinkWeights implements Event {
    
    
    private List<LinkInfo> links;
    
    // Constructor for creating new LinkWeights message
    public LinkWeights(List<LinkInfo> links) {
        this.links = links;
    }
    
    // Constructor for unmarshalling
    public LinkWeights(byte[] data) throws IOException {
        this.links = new ArrayList<>();
        unmarshallBytes(data);
    }
    
    //Marshing Method 
    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(baOutputStream);
        
       
        dout.writeInt(Protocol.LINK_WEIGHTS);
    
        dout.writeInt(links.size());
        
        // 3. Write each link info
        for (LinkInfo link : links) {
           
            byte[] nodeABytes = link.getNodeA().getBytes();
            dout.writeInt(nodeABytes.length);
            dout.write(nodeABytes);
            
            // Write nodeB (ip:port)
            byte[] nodeBBytes = link.getNodeB().getBytes();
            dout.writeInt(nodeBBytes.length);
            dout.write(nodeBBytes);
            
            dout.writeInt(link.getWeight());
        }
        
        dout.flush();
        byte[] data = baOutputStream.toByteArray();
        dout.close();
        baOutputStream.close();
        
        return data;
    }
    
    // UNMARSHALLING: Convert byte array to object
    private void unmarshallBytes(byte[] data) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(baInputStream);
        
        // 1. Read and verify message type
        int messageType = din.readInt();
        if (messageType != Protocol.LINK_WEIGHTS) {
            throw new IOException("Invalid message type for LinkWeights");
        }
        
        // 2. Read number of links
        int numLinks = din.readInt();
        
        // 3. Read each link info
        for (int i = 0; i < numLinks; i++) {
            int nodeALength = din.readInt();
            byte[] nodeABytes = new byte[nodeALength];
            din.readFully(nodeABytes);
            String nodeA = new String(nodeABytes);
            

            int nodeBLength = din.readInt();
            byte[] nodeBBytes = new byte[nodeBLength];
            din.readFully(nodeBBytes);
            String nodeB = new String(nodeBBytes);
            int weight = din.readInt();
            
            links.add(new LinkInfo(nodeA, nodeB, weight));
        }
        
        din.close();
        baInputStream.close();
    }
    
    @Override
    public int getType() {
        return Protocol.LINK_WEIGHTS;
    }
    
    public List<LinkInfo> getLinks() {
        return links;
    }
    
    public int getNumberOfLinks() {
        return links.size();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LinkWeights[").append(links.size()).append(" links]:\n");
        for (LinkInfo link : links) {
            sb.append("  ").append(link.toString()).append("\n");
        }
        return sb.toString();
    }

    public static class LinkInfo {
        private String nodeA;
        private String nodeB;
        private int weight;
        
        public LinkInfo(String nodeA, String nodeB, int weight) {
            this.nodeA = nodeA;
            this.nodeB = nodeB;
            this.weight = weight;
        }
        
        public String getNodeA() { return nodeA; }
        public String getNodeB() { return nodeB; }
        public int getWeight() { return weight; }
   
        @Override
    public String toString() {
        return nodeA + " " + nodeB + " " + weight;
    }
}
    }
