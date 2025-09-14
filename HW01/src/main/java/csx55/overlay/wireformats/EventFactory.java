package csx55.overlay.wireformats;

import java.io.IOException;

public class EventFactory {
    
    private static EventFactory instance;
    
    private EventFactory() {}
    
    //Singleton method to get the instance
    public static synchronized EventFactory getInstance() {
        if (instance == null) {
            instance = new EventFactory();
        }
        return instance;
    }
    
    public static Event createEvent(byte[] data) throws IOException {
        int messageType = java.nio.ByteBuffer.wrap(data, 0, 4).getInt();
        
        switch (messageType) {
            case Protocol.REGISTER_REQUEST:
                return new Register(data);
                
            case Protocol.REGISTER_RESPONSE:
                return new Register(data); 
                
            // case Protocol.DEREGISTER_REQUEST:
            //     return new Deregister(data);
                
            case Protocol.MESSAGING_NODES_LIST:
                return new MessagingNodesList(data);
                
            // case Protocol.LINK_WEIGHTS:
            //     return new LinkWeights(data);
                
            // case Protocol.TASK_INITIATE:
            //     return new TaskInitiate(data);
                
            // case Protocol.TASK_COMPLETE:
            //     return new TaskComplete(data);

            // case Protocol.OVERLAY_MESSAGE:
            //     return new OverlayMessage(data);
                
            // case Protocol.PULL_TRAFFIC_SUMMARY:
            //     return new TaskSummaryRequest(data); 
                
            // case Protocol.TRAFFIC_SUMMARY:
            //     return new TaskSummaryResponse(data); 
                
            default:
                throw new IOException("EVENT FACTORY : Unknown message type: " + messageType);
        }
    }
}