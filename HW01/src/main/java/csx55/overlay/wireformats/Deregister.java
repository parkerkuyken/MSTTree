package csx55.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public class Deregister implements Event {
    
    private int messageType;
    private String ipAddress;
    private int portNumber;

    public Deregister(String ipAddress, int portNumber) {
        this.messageType = Protocol.DEREGISTER_REQUEST;
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
    }

    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baOutputStream);
        DataOutputStream dout = new DataOutputStream(bos);
    
        try {
            dout.writeInt(messageType);
            
            byte[] ipBytes = ipAddress.getBytes();
            dout.writeInt(ipBytes.length);
            dout.write(ipBytes);
            
            // Write port number
            dout.writeInt(portNumber);
            
            dout.flush(); 
            return baOutputStream.toByteArray();
            
        } finally {
          
            dout.close();
            bos.close();
            baOutputStream.close();
        }
    }

    public Deregister(byte[] data) throws IOException {
        unmarshallBytes(data);
    }

    private void unmarshallBytes(byte[] data) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        BufferedInputStream bis = new BufferedInputStream(baInputStream);
        DataInputStream din = new DataInputStream(bis);
        
        try {
            this.messageType = din.readInt();
            
            int ipLength = din.readInt();
            byte[] ipBytes = new byte[ipLength];
            din.readFully(ipBytes);
            this.ipAddress = new String(ipBytes);
                
            this.portNumber = din.readInt();
        } finally {
            din.close();
            bis.close();
            baInputStream.close();
        }
    }

    @Override
    public String toString() {
        return "Deregister Request[ip=" + ipAddress + ", port=" + portNumber + "]";
}
  
    

    public int getMessageType() {
        return messageType;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public int getPortNumber() {
        return portNumber;
    }

    @Override
    public int getType() {
        return messageType;
    }
}