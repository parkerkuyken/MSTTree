package csx55.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class Register implements Event {
    private int messageType;  
    private String ipAddress;
    private int portNumber;
    private byte statusCode;  //response only
    private String additionalInfo;  //response only
    
    //REGISTER REQUEST
    public Register(String ipAddress, int portNumber) {
        this.messageType = Protocol.REGISTER_REQUEST;
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
    }
    
    //REGISTER RESPONSE
    public Register(byte statusCode, String additionalInfo) {
        this.messageType = Protocol.REGISTER_RESPONSE;
        this.statusCode = statusCode;
        this.additionalInfo = additionalInfo;
    }
    
   
    public Register(byte[] data) throws IOException {
        unmarshallBytes(data);
    }
    
    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(baOutputStream);
        
        dout.writeInt(messageType);
        
        if (messageType == Protocol.REGISTER_REQUEST) {
            byte[] ipBytes = ipAddress.getBytes();
            dout.writeInt(ipBytes.length);
            dout.write(ipBytes);
            dout.writeInt(portNumber);
            
        } else if (messageType == Protocol.REGISTER_RESPONSE) {
            dout.writeByte(statusCode);
            
            byte[] infoBytes = additionalInfo.getBytes();
            dout.writeInt(infoBytes.length);
            dout.write(infoBytes);
        }
        
        dout.flush();
        byte[] data = baOutputStream.toByteArray();
        dout.close();
        baOutputStream.close();
        
        return data;
    }
    
    private void unmarshallBytes(byte[] data) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(baInputStream);
        
        this.messageType = din.readInt();
        
        if (this.messageType == Protocol.REGISTER_REQUEST) {
            int ipLength = din.readInt();
            byte[] ipBytes = new byte[ipLength];
            din.readFully(ipBytes);
            this.ipAddress = new String(ipBytes);
            
            this.portNumber = din.readInt();
            
        } else if (this.messageType == Protocol.REGISTER_RESPONSE) {
            this.statusCode = din.readByte();
            
            int infoLength = din.readInt();
            byte[] infoBytes = new byte[infoLength];
            din.readFully(infoBytes);
            this.additionalInfo = new String(infoBytes);
        }
        
        din.close();
        baInputStream.close();
    }
    
    @Override
    public int getType() {
        return messageType;
    }
    
    public String getIpAddress() { 
        return ipAddress; 
    }

    public int getPortNumber(){ 
        return portNumber;
    }

    public byte getStatusCode(){ 
        return statusCode; 
    }

    public String getAdditionalInfo(){ 
        return additionalInfo; 
    }
}