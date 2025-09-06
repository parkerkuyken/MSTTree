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

    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(baOutputStream);
        
        dout.writeInt(Protocol.TASK_COMPLETE);
        
        byte[] ipBytes = ipAddress.getBytes();
        dout.writeInt(ipBytes.length);
        dout.write(ipBytes);
         
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
        
        int messageType = din.readInt();
        if (messageType != Protocol.TASK_COMPLETE) {
            throw new IOException("Invalid message type for TaskComplete: " + messageType);
        }

        int ipLength = din.readInt();
        byte[] ipBytes = new byte[ipLength];
        din.readFully(ipBytes);
        this.ipAddress = new String(ipBytes);
        
        this.portNumber = din.readInt();

        din.close();
        baInputStream.close();
    }

    @Override
    public int getType() {
        return Protocol.TASK_COMPLETE; 
    }


    public String getIpAddress(){
         return ipAddress; 
        }
    public int getPortNumber(){
         return portNumber; 
        }
    
    @Override
    public String toString(){
        return "TaskComplete[ip=" + ipAddress + ", port=" + portNumber + "]";
    }
}