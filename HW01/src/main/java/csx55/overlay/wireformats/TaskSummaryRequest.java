package csx55.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TaskSummaryRequest implements Event {
    
    public TaskSummaryRequest() {
    }
    
    public TaskSummaryRequest(byte[] data) throws IOException {
        if (data == null) {
            throw new IOException("Data cannot be null for TaskSummaryRequest");
        }
        unmarshallBytes(data);
    }
    
    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(baOutputStream);
        
        dout.writeInt(Protocol.PULL_TRAFFIC_SUMMARY);
        
        dout.flush();
        byte[] data = baOutputStream.toByteArray();
        dout.close();
        baOutputStream.close();
        
        return data;
    }
    
    private void unmarshallBytes(byte[] data) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(baInputStream);
        
        int messageType = din.readInt();
        if (messageType != Protocol.PULL_TRAFFIC_SUMMARY) {
            throw new IOException("Invalid message type for PullTrafficSummary: " + messageType);
        }
        
        din.close();
        baInputStream.close();
    }
    
    @Override
    public int getType() {
        return Protocol.PULL_TRAFFIC_SUMMARY;
    }
    
    @Override
    public String toString() {
        return "PullTrafficSummary[]";
    }
}