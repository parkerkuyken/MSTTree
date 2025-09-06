package csx55.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TaskInitiate implements Event {
    private int rounds;
    
  
    public TaskInitiate(int rounds) {
        this.rounds = rounds;
    }
    

    public TaskInitiate(byte[] data) throws IOException {
        unmarshallBytes(data);
    }
  
    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(baOutputStream);
        
        // 1. Write fixed message type
        dout.writeInt(Protocol.TASK_INITIATE);
        
        // 2. Write rounds
        dout.writeInt(rounds);
        
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
    
        int messageType = din.readInt();
        if (messageType != Protocol.TASK_INITIATE) {
            throw new IOException("error" + messageType);
        }
        
        // 2. Read rounds
        this.rounds = din.readInt();
        
        din.close();
        baInputStream.close();
    }
    
    @Override
    public int getType() {
        return Protocol.TASK_INITIATE; 
    }
    
    public int getRounds() {
        return rounds;
    }
}