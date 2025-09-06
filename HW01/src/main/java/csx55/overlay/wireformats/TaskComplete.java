package csx55.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TaskComplete implements Event {
    
    private String ipAddress;
    private int portNumber;

    public TaskComplete(String ipAddress, int portNumber) {
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
    }

    public TaskComplete(byte[] data) throws IOException {
        unmarshallBytes(data);
    }

    // Marshalling Method
    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(baOutputStream);
        
        // 1. Write fixed message type
        dout.writeInt(Protocol.TASK_COMPLETE);
        
        // 2. Write IP address (length + bytes)
        byte[] ipBytes = ipAddress.getBytes();
        dout.writeInt(ipBytes.length);
        dout.write(ipBytes);
         
        // 3. Write port number
        dout.writeInt(portNumber);
        
        dout.flush();
        byte[] data = baOutputStream.toByteArray();
        dout.close();
        baOutputStream.close();
        
        return data;
    }

    private void unmarshallBytes(byte[] data) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(baInputStream);
        
        // 1. Read and validate message type
        int messageType = din.readInt();
        if (messageType != Protocol.TASK_COMPLETE) {
            throw new IOException("Invalid message type for TaskComplete: " + messageType);
        }

        // 2. Read IP address
        int ipLength = din.readInt();
        byte[] ipBytes = new byte[ipLength];
        din.readFully(ipBytes);
        this.ipAddress = new String(ipBytes);
        
        // 3. Read port number
        this.portNumber = din.readInt();

        din.close();
        baInputStream.close();
    }

    @Override
    public int getType() {
        return Protocol.TASK_COMPLETE; // Always returns fixed value
    }

    // Getters
    public String getIpAddress() { return ipAddress; }
    public int getPortNumber() { return portNumber; }
    
    @Override
    public String toString() {
        return "TaskComplete[ip=" + ipAddress + ", port=" + portNumber + "]";
    }
}