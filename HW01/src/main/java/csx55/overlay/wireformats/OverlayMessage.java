// package csx55.overlay.wireformats;

// import csx55.overlay.node.MessagingNode;
// import java.io.ByteArrayInputStream;
// import java.io.ByteArrayOutputStream;
// import java.io.DataInputStream;
// import java.io.DataOutputStream;
// import java.io.IOException;

// public class OverlayMessage implements Event {
//     private MessagingNode sourceNode;
//     private MessagingNode destinationNode;
//     private MessagingNode[] path;
//     private int currentHopIndex;
//     private int payload;
//     private long messageId;
    
//     public OverlayMessage(MessagingNode sourceNode, MessagingNode destinationNode, 
//                          MessagingNode[] path, int payload, long messageId) {
//         this.sourceNode = sourceNode;
//         this.destinationNode = destinationNode;
//         this.path = path;
//         this.currentHopIndex = 0;
//         this.payload = payload;
//         this.messageId = messageId;
//     }
    
//     public OverlayMessage(byte[] data) throws IOException {
//         unmarshallBytes(data);
//     }
    
//     @Override
//     public byte[] getBytes() throws IOException {
//         ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
//         DataOutputStream dout = new DataOutputStream(baOutputStream);
        
//         dout.writeInt(Protocol.OVERLAY_MESSAGE);
        
//         // Write source node - we'll need a way to serialize MessagingNode
//         String sourceAddress = serializeNode(sourceNode);
//         byte[] sourceBytes = sourceAddress.getBytes();
//         dout.writeInt(sourceBytes.length);
//         dout.write(sourceBytes);
        
//         // Write destination node
//         String destAddress = serializeNode(destinationNode);
//         byte[] destBytes = destAddress.getBytes();
//         dout.writeInt(destBytes.length);
//         dout.write(destBytes);
        
//         // Write path
//         dout.writeInt(path.length);
//         for (MessagingNode node : path) {
//             String nodeAddress = serializeNode(node);
//             byte[] nodeBytes = nodeAddress.getBytes();
//             dout.writeInt(nodeBytes.length);
//             dout.write(nodeBytes);
//         }
        
//         dout.writeInt(currentHopIndex);
//         dout.writeInt(payload);
//         dout.writeLong(messageId);
        
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
//         if (messageType != Protocol.OVERLAY_MESSAGE) {
//             throw new IOException("Invalid message type for OverlayMessage: " + messageType);
//         }
        
//         // Read source node
//         int sourceLength = din.readInt();
//         byte[] sourceBytes = new byte[sourceLength];
//         din.readFully(sourceBytes);
//         this.sourceNode = deserializeNode(new String(sourceBytes));
        
//         // Read destination node
//         int destLength = din.readInt();
//         byte[] destBytes = new byte[destLength];
//         din.readFully(destBytes);
//         this.destinationNode = deserializeNode(new String(destBytes));
        
//         // Read path
//         int pathLength = din.readInt();
//         this.path = new MessagingNode[pathLength];
//         for (int i = 0; i < pathLength; i++) {
//             int nodeLength = din.readInt();
//             byte[] nodeBytes = new byte[nodeLength];
//             din.readFully(nodeBytes);
//             this.path[i] = deserializeNode(new String(nodeBytes));
//         }
        
//         this.currentHopIndex = din.readInt();
//         this.payload = din.readInt();
//         this.messageId = din.readLong();
        
//         din.close();
//         baInputStream.close();
//     }
    
//     // Helper methods to serialize/deserialize MessagingNode
//     private String serializeNode(MessagingNode node) {
//         // You'll need to implement this in MessagingNode class
//         // Example: return node.getIp() + ":" + node.getPort();
//         return node.toString(); // Assuming toString() returns "ip:port"
//     }
    
//     private MessagingNode deserializeNode(String nodeString) {
//         // You'll need to implement this in MessagingNode class
//         // Example: String[] parts = nodeString.split(":");
//         // return new MessagingNode(parts[0], Integer.parseInt(parts[1]));
//         return new MessagingNode(nodeString); // Assuming constructor takes "ip:port"
//     }
    
//     @Override
//     public int getType() {
//         return Protocol.OVERLAY_MESSAGE;
//     }
    
//     // Getters
//     public MessagingNode getSourceNode() { return sourceNode; }
//     public MessagingNode getDestinationNode() { return destinationNode; }
//     public MessagingNode[] getPath() { return path; }
//     public int getCurrentHopIndex() { return currentHopIndex; }
//     public int getPayload() { return payload; }
//     public long getMessageId() { return messageId; }
    
//     // Helper methods for routing
//     public MessagingNode getCurrentNode() {
//         return path[currentHopIndex];
//     }
    
//     public MessagingNode getNextHop() {
//         if (currentHopIndex < path.length - 1) {
//             return path[currentHopIndex + 1];
//         }
//         return null;
//     }
    
//     public boolean isAtDestination() {
//         return currentHopIndex == path.length - 1;
//     }
    
//     public OverlayMessage createNextHopMessage() throws IOException {
//         OverlayMessage nextMessage = new OverlayMessage(
//             sourceNode, destinationNode, path, payload, messageId
//         );
//         nextMessage.currentHopIndex = this.currentHopIndex + 1;
//         return nextMessage;
//     }
    
//     @Override
//     public String toString() {
//         return "OverlayMessage[src=" + sourceNode + ", dest=" + destinationNode + 
//                ", hops=" + currentHopIndex + "/" + (path.length - 1) + 
//                ", payload=" + payload + ", id=" + messageId + "]";
//     }
// }