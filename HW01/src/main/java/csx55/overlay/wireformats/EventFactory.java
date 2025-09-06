package csx55.overlay.wireformats;

import java.io.IOException;

public class EventFactory {
    
    public static Event createEvent(byte[] data) throws IOException {
        if (data == null || data.length < 4) {
            throw new IOException("Invalid message data: null or too short");
        }
        
        // Read message type from first 4 bytes
        int messageType = java.nio.ByteBuffer.wrap(data, 0, 4).getInt();
        
        switch (messageType) {
            case Protocol.REGISTER_REQUEST:
                return new Register(data);
                
            case Protocol.REGISTER_RESPONSE:
                return new Register(data); // Assuming Register handles both request/response
                
            case Protocol.DEREGISTER_REQUEST:
                return new Deregister(data);
                
            case Protocol.MESSAGING_NODES_LIST:
                return new MessagingNodesList(data);
                
            case Protocol.LINK_WEIGHTS:
                return new LinkWeights(data);
                
            case Protocol.TASK_INITIATE:
                return new TaskInitiate(data);
                
            case Protocol.TASK_COMPLETE:
                return new TaskComplete(data);
                
            // case Protocol.PULL_TRAFFIC_SUMMARY:
            //     return new TaskSummaryRequest(data); // Assuming this is PULL_TRAFFIC_SUMMARY
                
            // case Protocol.TRAFFIC_SUMMARY:
            //     return new TaskSummaryResponse(data); // Assuming this is TRAFFIC_SUMMARY
                
            default:
                throw new IOException("Unknown message type: " + messageType);
        }
    }
}