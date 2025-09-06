package csx55.overlay.wireformats;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class TaskCompleteTest {

    @Test
    public void testConstructorAndGetters() {
        String testIP = "192.168.1.100";
        int testPort = 8080;

        TaskComplete taskComplete = new TaskComplete(testIP, testPort);

        assertEquals(Protocol.TASK_COMPLETE, taskComplete.getType(), "Message type should be TASK_COMPLETE");
        assertEquals(testIP, taskComplete.getIpAddress(), "IP address should match");
        assertEquals(testPort, taskComplete.getPortNumber(), "Port number should match");
    }

    @Test
    public void testMarshallingAndUnmarshalling() throws IOException {
        String originalIP = "10.0.0.5";
        int originalPort = 9090;

        TaskComplete original = new TaskComplete(originalIP, originalPort);
        byte[] data = original.getBytes();
        TaskComplete reconstructed = new TaskComplete(data);

        assertEquals(original.getType(), reconstructed.getType(), "Message type should be preserved");
        assertEquals(original.getIpAddress(), reconstructed.getIpAddress(), "IP address should be preserved");
        assertEquals(original.getPortNumber(), reconstructed.getPortNumber(), "Port number should be preserved");
    }

    @Test
    public void testMarshallingAndUnmarshallingWithDifferentIP() throws IOException {
        String testIP = "255.255.255.255";
        int testPort = 65535;

        TaskComplete original = new TaskComplete(testIP, testPort);
        byte[] data = original.getBytes();
        TaskComplete reconstructed = new TaskComplete(data);

        assertEquals(testIP, reconstructed.getIpAddress(), "Broadcast IP should be preserved");
        assertEquals(testPort, reconstructed.getPortNumber(), "Max port should be preserved");
    }

    @Test
    public void testMarshallingAndUnmarshallingWithLocalhost() throws IOException {
        String testIP = "localhost";
        int testPort = 1234;

        TaskComplete original = new TaskComplete(testIP, testPort);
        byte[] data = original.getBytes();
        TaskComplete reconstructed = new TaskComplete(data);

        assertEquals(testIP, reconstructed.getIpAddress(), "Localhost IP should be preserved");
        assertEquals(testPort, reconstructed.getPortNumber(), "Port should be preserved");
    }

    @Test
    public void testMarshallingAndUnmarshallingWithEmptyIP() throws IOException {
        String testIP = "";
        int testPort = 0;

        TaskComplete original = new TaskComplete(testIP, testPort);
        byte[] data = original.getBytes();
        TaskComplete reconstructed = new TaskComplete(data);

        assertEquals(testIP, reconstructed.getIpAddress(), "Empty IP should be preserved");
        assertEquals(testPort, reconstructed.getPortNumber(), "Zero port should be preserved");
    }

    @Test
    public void testMarshallingAndUnmarshallingWithLongIP() throws IOException {
        String testIP = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";
        int testPort = 54321;

        TaskComplete original = new TaskComplete(testIP, testPort);
        byte[] data = original.getBytes();
        TaskComplete reconstructed = new TaskComplete(data);

        assertEquals(testIP, reconstructed.getIpAddress(), "IPv6 address should be preserved");
        assertEquals(testPort, reconstructed.getPortNumber(), "Port should be preserved");
    }

    @Test
    public void testToString() {
        String testIP = "192.168.1.1";
        int testPort = 8080;

        TaskComplete taskComplete = new TaskComplete(testIP, testPort);
        String result = taskComplete.toString();

        assertTrue(result.contains(testIP), "toString should contain IP");
        assertTrue(result.contains(String.valueOf(testPort)), "toString should contain port");
        assertTrue(result.contains("TaskComplete"), "toString should indicate TaskComplete");
    }

    @Test
    public void testInvalidData() {
        byte[] invalidData = new byte[5]; // Too short for a valid message
        assertThrows(IOException.class, () -> {
            new TaskComplete(invalidData);
        }, "Should throw IOException for invalid data");
    }

    @Test
    public void testWrongMessageType() throws IOException {
        // Create a different message type and try to parse as TaskComplete
        String testIP = "192.168.1.50";
        int testPort = 5000;

        TaskComplete original = new TaskComplete(testIP, testPort);
        byte[] data = original.getBytes();

        // Corrupt the message type in the byte array
        data[0] = (byte) 0xFF; // Set first byte to invalid message type

        assertThrows(IOException.class, () -> {
            new TaskComplete(data);
        }, "Should throw IOException for wrong message type");
    }

    @Test
    public void testDataIntegrity() throws IOException {
        String testIP = "192.168.1.50";
        int testPort = 5000;

        TaskComplete original = new TaskComplete(testIP, testPort);
        byte[] data = original.getBytes();

        // Data should be at least: 4 bytes (message type) + 4 bytes (IP length) + 4
        // bytes (port)
        assertTrue(data.length >= 12, "Data should be at least 12 bytes");

        // Verify message type is correct in the byte array
        int messageType = (data[0] << 24) | ((data[1] & 0xFF) << 16) | ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);
        assertEquals(Protocol.TASK_COMPLETE, messageType, "Message type in byte array should be TASK_COMPLETE");
    }

    @Test
    public void testMultipleSerializationRounds() throws IOException {
        String testIP = "8.8.8.8";
        int testPort = 53;

        TaskComplete original = new TaskComplete(testIP, testPort);

        for (int i = 0; i < 5; i++) {
            byte[] data = original.getBytes();
            TaskComplete reconstructed = new TaskComplete(data);

            assertEquals(testIP, reconstructed.getIpAddress(), "IP should remain consistent through round " + i);
            assertEquals(testPort, reconstructed.getPortNumber(), "Port should remain consistent through round " + i);
            assertEquals(Protocol.TASK_COMPLETE, reconstructed.getType(),
                    "Type should remain consistent through round " + i);
        }
    }

    @Test
    public void testEventFactoryIntegration() throws IOException {
        String testIP = "10.10.10.10";
        int testPort = 9999;

        TaskComplete original = new TaskComplete(testIP, testPort);
        byte[] data = original.getBytes();

        // Use EventFactory to reconstruct
        Object reconstructed = EventFactory.createEvent(data);

        assertTrue(reconstructed instanceof TaskComplete, "EventFactory should create TaskComplete instance");
        TaskComplete taskComplete = (TaskComplete) reconstructed;

        assertEquals(testIP, taskComplete.getIpAddress(), "IP should be preserved through EventFactory");
        assertEquals(testPort, taskComplete.getPortNumber(), "Port should be preserved through EventFactory");
    }

    @Test
    public void testEdgeCasePortNumbers() throws IOException {
        String testIP = "127.0.0.1";

        // Test minimum port
        TaskComplete minPort = new TaskComplete(testIP, 1);
        byte[] minData = minPort.getBytes();
        TaskComplete minReconstructed = new TaskComplete(minData);
        assertEquals(1, minReconstructed.getPortNumber(), "Minimum port should be preserved");

        // Test maximum port
        TaskComplete maxPort = new TaskComplete(testIP, 65535);
        byte[] maxData = maxPort.getBytes();
        TaskComplete maxReconstructed = new TaskComplete(maxData);
        assertEquals(65535, maxReconstructed.getPortNumber(), "Maximum port should be preserved");
    }

    @Test
    public void testSpecialIPAddresses() throws IOException {
        // Test various special IP addresses
        String[] specialIPs = {
                "0.0.0.0",
                "127.0.0.1",
                "255.255.255.255",
                "192.168.0.1",
                "10.0.0.1",
                "172.16.0.1"
        };

        for (String ip : specialIPs) {
            TaskComplete original = new TaskComplete(ip, 8080);
            byte[] data = original.getBytes();
            TaskComplete reconstructed = new TaskComplete(data);

            assertEquals(ip, reconstructed.getIpAddress(), "Special IP " + ip + " should be preserved");
        }
    }
}