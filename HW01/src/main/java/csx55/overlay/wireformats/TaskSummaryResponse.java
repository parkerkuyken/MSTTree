// package csx55.overlay.wireformats;

// import csx55.overlay.node.MessagingNode;
// import java.io.ByteArrayInputStream;
// import java.io.ByteArrayOutputStream;
// import java.io.DataInputStream;
// import java.io.DataOutputStream;
// import java.io.IOException;

// public class TaskSummaryResponse implements Event {
    
//     private MessagingNode node;
//     private int messagesSent;
//     private long sendSummation;
//     private int messagesReceived;
//     private long receiveSummation;
//     private int messagesRelayed;
    
    
//     public TaskSummaryResponse(MessagingNode node, int messagesSent, long sendSummation,
//                         int messagesReceived, long receiveSummation, int messagesRelayed) {
//         this.node = node;
//         this.messagesSent = messagesSent;
//         this.sendSummation = sendSummation;
//         this.messagesReceived = messagesReceived;
//         this.receiveSummation = receiveSummation;
//         this.messagesRelayed = messagesRelayed;
//     }
    
//     public TaskSummaryResponse(byte[] data) throws IOException {
//         if (data == null) {
//             throw new IOException("Data cannot be null for TaskSummaryResponse");
//         }
//         unmarshallBytes(data);
//     }
    
//     @Override
//     public byte[] getBytes() throws IOException {
//         ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
//         DataOutputStream dout = new DataOutputStream(baOutputStream);
        
//         dout.writeInt(Protocol.TRAFFIC_SUMMARY);
        
//         String nodeAddress = node.toString();
//         byte[] nodeBytes = nodeAddress.getBytes();
//         dout.writeInt(nodeBytes.length);
//         dout.write(nodeBytes);
        
//         dout.writeInt(messagesSent);
//         dout.writeLong(sendSummation);
//         dout.writeInt(messagesReceived);
//         dout.writeLong(receiveSummation);
//         dout.writeInt(messagesRelayed);
        
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
//         if (messageType != Protocol.TRAFFIC_SUMMARY) {
//             throw new IOException("Invalid message type for TrafficSummary: " + messageType);
//         }
        
//         // Read node information
//         int nodeLength = din.readInt();
//         byte[] nodeBytes = new byte[nodeLength];
//         din.readFully(nodeBytes);
//         String nodeAddress = new String(nodeBytes);
//         this.node = new MessagingNode(nodeAddress);
        
//         // Read traffic statistics
//         this.messagesSent = din.readInt();
//         this.sendSummation = din.readLong();
//         this.messagesReceived = din.readInt();
//         this.receiveSummation = din.readLong();
//         this.messagesRelayed = din.readInt();
        
//         din.close();
//         baInputStream.close();
//     }
    
//     @Override
//     public int getType() {
//         return Protocol.TRAFFIC_SUMMARY;
//     }
    
//     // Getters
//     public MessagingNode getNode() { return node; }
//     public int getMessagesSent() { return messagesSent; }
//     public long getSendSummation() { return sendSummation; }
//     public int getMessagesReceived() { return messagesReceived; }
//     public long getReceiveSummation() { return receiveSummation; }
//     public int getMessagesRelayed() { return messagesRelayed; }
    
//     @Override
//     public String toString() {
//         return String.format(
//             "TaskSummaryResponse[node=%s, sent=%d, sendSum=%,d, received=%d, recvSum=%,d, relayed=%d]",
//             node, messagesSent, sendSummation, messagesReceived, receiveSummation, messagesRelayed
//         );
//     }
// }