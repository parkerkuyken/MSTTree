package csx55.overlay.wireformats;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

public class TaskSummaryRequestTest {

    @Test
    public void testMarshallingUnmarshalling() throws IOException {
        TaskSummaryRequest original = new TaskSummaryRequest();
        byte[] data = original.getBytes();
        TaskSummaryRequest reconstructed = new TaskSummaryRequest(data);
        
        assertEquals(Protocol.PULL_TRAFFIC_SUMMARY, reconstructed.getType());
        assertEquals(original.getType(), reconstructed.getType());
    }

    @Test
    public void testEventFactoryIntegration() throws IOException {
        TaskSummaryRequest original = new TaskSummaryRequest();
        byte[] data = original.getBytes();
        
        // Use EventFactory to reconstruct
        Object reconstructed = EventFactory.createEvent(data);
        
        assertTrue(reconstructed instanceof TaskSummaryRequest);
        TaskSummaryRequest request = (TaskSummaryRequest) reconstructed;
        assertEquals(Protocol.PULL_TRAFFIC_SUMMARY, request.getType());
    }

    
    @Test
    public void testEmptyData() {
        byte[] emptyData = new byte[0];
        assertThrows(IOException.class, () -> {
            new TaskSummaryRequest(emptyData);
        });
    }

    @Test
    public void testShortData() {
        byte[] shortData = new byte[3]; // Less than 4 bytes needed for message type
        assertThrows(IOException.class, () -> {
            new TaskSummaryRequest(shortData);
        });
    }

    @Test
    public void testMultipleInstances() throws IOException {
        // Test that multiple instances work correctly
        TaskSummaryRequest request1 = new TaskSummaryRequest();
        TaskSummaryRequest request2 = new TaskSummaryRequest();
        
        byte[] data1 = request1.getBytes();
        byte[] data2 = request2.getBytes();
        
        // Both should have same content
        assertArrayEquals(data1, data2);
        
        TaskSummaryRequest reconstructed1 = new TaskSummaryRequest(data1);
        TaskSummaryRequest reconstructed2 = new TaskSummaryRequest(data2);
        
        assertEquals(reconstructed1.getType(), reconstructed2.getType());
    }

    @Test
    public void testNullData() {
        assertThrows(IOException.class, () -> {
            new TaskSummaryRequest(null);
        });
    }

    @Test
    public void testDataIntegrity() throws IOException {
        TaskSummaryRequest original = new TaskSummaryRequest();
        byte[] data = original.getBytes();
        
        // Verify data length is exactly 4 bytes (just message type)
        assertEquals(4, data.length);
        
        // Verify message type is correct in byte array
        int messageType = (data[0] << 24) | ((data[1] & 0xFF) << 16) | ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);
        assertEquals(Protocol.PULL_TRAFFIC_SUMMARY, messageType);
    }
}