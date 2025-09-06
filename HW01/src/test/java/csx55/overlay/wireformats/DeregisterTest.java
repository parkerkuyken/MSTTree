package csx55.overlay.wireformats;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public class DeregisterTest {

    @Test
    public void testConstructorAndGetters() {
        String testIP = "192.168.1.100";
        int testPort = 8080;
        int messageType = Protocol.DEREGISTER_REQUEST;
        
        Deregister deregister = new Deregister(messageType, testIP, testPort);
        
        assertEquals(messageType, deregister.getMessageType(), "Message type should match");
        assertEquals(testIP, deregister.getIpAddress(), "IP address should match");
        assertEquals(testPort, deregister.getPortNumber(), "Port number should match");
    }

    @Test
    public void testMarshallingAndUnmarshalling() throws IOException {
        String originalIP = "10.0.0.5";
        int originalPort = 9090;
        int messageType = Protocol.DEREGISTER_REQUEST;
        
        Deregister original = new Deregister(messageType, originalIP, originalPort);
        byte[] data = original.getBytes();
        Deregister reconstructed = new Deregister(data);
        
        assertEquals(original.getMessageType(), reconstructed.getMessageType(), "Message type should be preserved");
        assertEquals(original.getIpAddress(), reconstructed.getIpAddress(), "IP address should be preserved");
        assertEquals(original.getPortNumber(), reconstructed.getPortNumber(), "Port number should be preserved");
    }

    @Test
    public void testMarshallingAndUnmarshallingWithDifferentIP() throws IOException {
        String testIP = "255.255.255.255";
        int testPort = 65535;
        int messageType = Protocol.DEREGISTER_REQUEST;
        
        Deregister original = new Deregister(messageType, testIP, testPort);
        byte[] data = original.getBytes();
        Deregister reconstructed = new Deregister(data);
        
        assertEquals(testIP, reconstructed.getIpAddress(), "Broadcast IP should be preserved");
        assertEquals(testPort, reconstructed.getPortNumber(), "Max port should be preserved");
    }

    @Test
    public void testMarshallingAndUnmarshallingWithLocalhost() throws IOException {
        String testIP = "localhost";
        int testPort = 1234;
        int messageType = Protocol.DEREGISTER_REQUEST;
        
        Deregister original = new Deregister(messageType, testIP, testPort);
        byte[] data = original.getBytes();
        Deregister reconstructed = new Deregister(data);
        
        assertEquals(testIP, reconstructed.getIpAddress(), "Localhost IP should be preserved");
        assertEquals(testPort, reconstructed.getPortNumber(), "Port should be preserved");
    }

    @Test
    public void testMarshallingAndUnmarshallingWithEmptyIP() throws IOException {
        String testIP = "";
        int testPort = 0;
        int messageType = Protocol.DEREGISTER_REQUEST;
        
        Deregister original = new Deregister(messageType, testIP, testPort);
        byte[] data = original.getBytes();
        Deregister reconstructed = new Deregister(data);
        
        assertEquals(testIP, reconstructed.getIpAddress(), "Empty IP should be preserved");
        assertEquals(testPort, reconstructed.getPortNumber(), "Zero port should be preserved");
    }

    @Test
    public void testMarshallingAndUnmarshallingWithLongIP() throws IOException {
        String testIP = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";
        int testPort = 54321;
        int messageType = Protocol.DEREGISTER_REQUEST;
        
        Deregister original = new Deregister(messageType, testIP, testPort);
        byte[] data = original.getBytes();
        Deregister reconstructed = new Deregister(data);
        
        assertEquals(testIP, reconstructed.getIpAddress(), "IPv6 address should be preserved");
        assertEquals(testPort, reconstructed.getPortNumber(), "Port should be preserved");
    }

    @Test
    public void testToString() {
        String testIP = "192.168.1.1";
        int testPort = 8080;
        int messageType = Protocol.DEREGISTER_REQUEST;
        
        Deregister deregister = new Deregister(messageType, testIP, testPort);
        String result = deregister.toString();
        
        assertTrue(result.contains(testIP), "toString should contain IP");
        assertTrue(result.contains(String.valueOf(testPort)), "toString should contain port");
        assertTrue(result.contains("Deregister Request"), "toString should indicate Deregister Request");
    }

    @Test
    public void testInvalidData() throws IOException {
        byte[] invalidData = new byte[5];
        assertThrows(IOException.class, () -> {
            new Deregister(invalidData);
        }, "Should throw IOException for invalid data");
    }

    @Test
    public void testDataIntegrity() throws IOException {
        String testIP = "192.168.1.50";
        int testPort = 5000;
        int messageType = Protocol.DEREGISTER_REQUEST;
        
        Deregister original = new Deregister(messageType, testIP, testPort);
        byte[] data = original.getBytes();
        
        assertTrue(data.length >= 12, "Data should be at least 12 bytes");
    }

   
      
    @Test
    public void testMultipleSerializationRounds() throws IOException {
        String testIP = "8.8.8.8";
        int testPort = 53;
        int messageType = Protocol.DEREGISTER_REQUEST;
        
        Deregister original = new Deregister(messageType, testIP, testPort);
        
        for (int i = 0; i < 5; i++) {
            byte[] data = original.getBytes();
            Deregister reconstructed = new Deregister(data);
            
            assertEquals(testIP, reconstructed.getIpAddress(), "IP should remain consistent through round " + i);
            assertEquals(testPort, reconstructed.getPortNumber(), "Port should remain consistent through round " + i);
        }
    }
}