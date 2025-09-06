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
        if (data == null) {
            throw new IOException("Data cannot be null");
        }
        unmarshallBytes(data);
    }
  
    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(baOutputStream);
        
        dout.writeInt(Protocol.TASK_INITIATE);
        dout.writeInt(rounds);
        
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
        if (messageType != Protocol.TASK_INITIATE) {
            throw new IOException("ERROR: " + messageType);
        }

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