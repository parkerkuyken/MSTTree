package csx55.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class Register implements Event {
    // Fields for both request and response
    private int messageType;  // REGISTER_REQUEST or REGISTER_RESPONSE
    private String ipAddress;
    private int portNumber;
    private byte statusCode;  // For response only
    private String additionalInfo;  // For response only
    
    // CONSTRUCTOR FOR REGISTER REQUEST
    public Register(String ipAddress, int portNumber) {
        this.messageType = Protocol.REGISTER_REQUEST;
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
    }
    
    // CONSTRUCTOR FOR REGISTER RESPONSE
    public Register(byte statusCode, String additionalInfo) {
        this.messageType = Protocol.REGISTER_RESPONSE;
        this.statusCode = statusCode;
        this.additionalInfo = additionalInfo;
    }
    
    // CONSTRUCTOR FOR UNMARSHALLING (from bytes)
    public Register(byte[] data) throws IOException {
        unmarshallBytes(data);
    }
    
    // MARSHALLING: Convert object to byte array
    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(baOutputStream);
        
        // 1. Always write message type first
        dout.writeInt(messageType);
        
        if (messageType == Protocol.REGISTER_REQUEST) {
            // 2. For REQUEST: Write IP address
            byte[] ipBytes = ipAddress.getBytes();
            dout.writeInt(ipBytes.length);
            dout.write(ipBytes);
            
            // 3. Write port number
            dout.writeInt(portNumber);
            
        } else if (messageType == Protocol.REGISTER_RESPONSE) {
            // 2. For RESPONSE: Write status code
            dout.writeByte(statusCode);
            
            // 3. Write additional info
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
    
    // UNMARSHALLING: Convert byte array to object
    private void unmarshallBytes(byte[] data) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(baInputStream);
        
        // 1. Read message type
        this.messageType = din.readInt();
        
        if (this.messageType == Protocol.REGISTER_REQUEST) {
            // 2. For REQUEST: Read IP address
            int ipLength = din.readInt();
            byte[] ipBytes = new byte[ipLength];
            din.readFully(ipBytes);
            this.ipAddress = new String(ipBytes);
            
            // 3. Read port number
            this.portNumber = din.readInt();
            
        } else if (this.messageType == Protocol.REGISTER_RESPONSE) {
            // 2. For RESPONSE: Read status code
            this.statusCode = din.readByte();
            
            // 3. Read additional info
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
    
    // Getters
    public String getIpAddress() { return ipAddress; }
    public int getPortNumber() { return portNumber; }
    public byte getStatusCode() { return statusCode; }
    public String getAdditionalInfo() { return additionalInfo; }
}