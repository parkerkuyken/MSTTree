// package csx55.overlay.wireformats;

// import csx55.overlay.node.MessagingNode;
// import java.io.ByteArrayInputStream;
// import java.io.ByteArrayOutputStream;
// import java.io.DataInputStream;
// import java.io.DataOutputStream;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;

// public class LinkWeights implements Event {
    
//     private List<LinkInfo> links;
    
//     public LinkWeights(List<LinkInfo> links) {
//         this.links = links;
//     }
    
//     public LinkWeights(byte[] data) throws IOException {
//         this.links = new ArrayList<>();
//         unmarshallBytes(data);
//     }
    
//     @Override
//     public byte[] getBytes() throws IOException {
//         ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
//         DataOutputStream dout = new DataOutputStream(baOutputStream);
        
//         dout.writeInt(Protocol.LINK_WEIGHTS);
//         dout.writeInt(links.size());
        
//         for (LinkInfo link : links) {
//             // Write nodeA (as "ip:port" string)
//             String nodeAAddress = link.getNodeA().toString();
//             byte[] nodeABytes = nodeAAddress.getBytes();
//             dout.writeInt(nodeABytes.length);
//             dout.write(nodeABytes);
            
//             // Write nodeB (as "ip:port" string)
//             String nodeBAddress = link.getNodeB().toString();
//             byte[] nodeBBytes = nodeBAddress.getBytes();
//             dout.writeInt(nodeBBytes.length);
//             dout.write(nodeBBytes);
            
//             dout.writeInt(link.getWeight());
//         }
        
//         dout.flush();
//         byte[] data = baOutputStream.toByteArray();
//         dout.close();
//         baOutputStream.close();
        
//         return data;
//     }
    
//     private void unmarshallBytes(byte[] data) throws IOException {
//         ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
//         DataInputStream din = new DataInputStream(baInputStream);
        
//         int messageType = din.readInt();
//         if (messageType != Protocol.LINK_WEIGHTS) {
//             throw new IOException("Invalid message type for LinkWeights");
//         }
        
//         int numLinks = din.readInt();
        
//         for (int i = 0; i < numLinks; i++) {
 
//             int nodeALength = din.readInt();
//             byte[] nodeABytes = new byte[nodeALength];
//             din.readFully(nodeABytes);
//             String nodeAAddress = new String(nodeABytes);
//             MessagingNode nodeA = new MessagingNode(nodeAAddress);
            
         
//             int nodeBLength = din.readInt();
//             byte[] nodeBBytes = new byte[nodeBLength];
//             din.readFully(nodeBBytes);
//             String nodeBAddress = new String(nodeBBytes);
//             MessagingNode nodeB = new MessagingNode(nodeBAddress);
    
//             int weight = din.readInt();
            
//             links.add(new LinkInfo(nodeA, nodeB, weight));
//         }
        
//         din.close();
//         baInputStream.close();
//     }
    
//     @Override
//     public int getType() {
//         return Protocol.LINK_WEIGHTS;
//     }
    
//     public List<LinkInfo> getLinks() {
//         return links;
//     }
    
//     public int getNumberOfLinks() {
//         return links.size();
//     }
    
//     @Override
//     public String toString() {
//         StringBuilder sb = new StringBuilder();
//         sb.append("LinkWeights[").append(links.size()).append(" links]:\n");
//         for (LinkInfo link : links) {
//             sb.append("  ").append(link.toString()).append("\n");
//         }
//         return sb.toString();
//     }

//     public static class LinkInfo {
//         private MessagingNode nodeA;
//         private MessagingNode nodeB;
//         private int weight;
        
//         public LinkInfo(MessagingNode nodeA, MessagingNode nodeB, int weight) {
//             this.nodeA = nodeA;
//             this.nodeB = nodeB;
//             this.weight = weight;
//         }
        
//         public MessagingNode getNodeA() { return nodeA; }
//         public MessagingNode getNodeB() { return nodeB; }
//         public int getWeight() { return weight; }
   
//         @Override
//         public String toString() {
//             return nodeA.toString() + " " + nodeB.toString() + " " + weight;
//         }
        
//         @Override
//         public boolean equals(Object obj) {
//             if (this == obj) return true;
//             if (!(obj instanceof LinkInfo)) return false;
//             LinkInfo other = (LinkInfo) obj;
//             return (nodeA.equals(other.nodeA) && nodeB.equals(other.nodeB)) ||
//                    (nodeA.equals(other.nodeB) && nodeB.equals(other.nodeA));
//         }
        
//         @Override
//         public int hashCode() {
//             // Consistent hash code for bidirectional links
//             return nodeA.hashCode() + nodeB.hashCode();
//         }
//     }
// }