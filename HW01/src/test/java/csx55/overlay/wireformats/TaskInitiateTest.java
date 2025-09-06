package csx55.overlay.wireformats;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

public class TaskInitiateTest {

    @Test
    public void testConstructorAndGetters() {
        int testRounds = 5;
        TaskInitiate taskInitiate = new TaskInitiate(testRounds);
        
        assertEquals(Protocol.TASK_INITIATE, taskInitiate.getType());
        assertEquals(testRounds, taskInitiate.getRounds());
    }

    @Test
    public void testMarshallingAndUnmarshalling() throws IOException {
        int originalRounds = 10;
        
        TaskInitiate original = new TaskInitiate(originalRounds);
        byte[] data = original.getBytes();
        TaskInitiate reconstructed = new TaskInitiate(data);
        
        assertEquals(original.getType(), reconstructed.getType());
        assertEquals(original.getRounds(), reconstructed.getRounds());
        assertEquals(Protocol.TASK_INITIATE, reconstructed.getType());
        assertEquals(originalRounds, reconstructed.getRounds());
    }

    @Test
    public void testSingleRound() throws IOException {
        TaskInitiate taskInitiate = new TaskInitiate(1);
        byte[] data = taskInitiate.getBytes();
        TaskInitiate reconstructed = new TaskInitiate(data);
        
        assertEquals(1, reconstructed.getRounds());
        assertEquals(Protocol.TASK_INITIATE, reconstructed.getType());
    }

    @Test
    public void testLargeNumberOfRounds() throws IOException {
        int largeRounds = 10000;
        
        TaskInitiate original = new TaskInitiate(largeRounds);
        byte[] data = original.getBytes();
        TaskInitiate reconstructed = new TaskInitiate(data);
        
        assertEquals(largeRounds, reconstructed.getRounds());
        assertEquals(Protocol.TASK_INITIATE, reconstructed.getType());
    }

    @Test
    public void testZeroRounds() throws IOException {
        TaskInitiate taskInitiate = new TaskInitiate(0);
        byte[] data = taskInitiate.getBytes();
        TaskInitiate reconstructed = new TaskInitiate(data);
        
        assertEquals(0, reconstructed.getRounds());
        assertEquals(Protocol.TASK_INITIATE, reconstructed.getType());
    }

    @Test
    public void testNegativeRounds() throws IOException {
        // Negative rounds should still work (though may not make practical sense)
        TaskInitiate taskInitiate = new TaskInitiate(-5);
        byte[] data = taskInitiate.getBytes();
        TaskInitiate reconstructed = new TaskInitiate(data);
        
        assertEquals(-5, reconstructed.getRounds());
        assertEquals(Protocol.TASK_INITIATE, reconstructed.getType());
    }

    // @Test
    // public void testInvalidMessageType() {
    //     // Create valid TaskInitiate first
    //     TaskInitiate original = new TaskInitiate(5);
    //     byte[] data = original.getBytes();
        
    //     // Corrupt the message type (first 4 bytes)
    //     data[0] = (byte) 0xFF; // Set to invalid message type
        
    //     assertThrows(IOException.class, () -> {
    //         new TaskInitiate(data);
    //     });
    // }

    @Test
    public void testEventFactoryIntegration() throws IOException {
        int testRounds = 8;
        
        TaskInitiate original = new TaskInitiate(testRounds);
        byte[] data = original.getBytes();
        
        // Use EventFactory to reconstruct
        Object reconstructed = EventFactory.createEvent(data);
        
        assertTrue(reconstructed instanceof TaskInitiate);
        TaskInitiate taskInitiate = (TaskInitiate) reconstructed;
        
        assertEquals(testRounds, taskInitiate.getRounds());
        assertEquals(Protocol.TASK_INITIATE, taskInitiate.getType());
    }

    @Test
    public void testDataIntegrity() throws IOException {
        int[] testRounds = {1, 5, 10, 100, 1000, -1, 0};
        
        for (int rounds : testRounds) {
            TaskInitiate original = new TaskInitiate(rounds);
            byte[] data = original.getBytes();
            TaskInitiate reconstructed = new TaskInitiate(data);
            
            assertEquals(rounds, reconstructed.getRounds(), 
                "Failed for rounds: " + rounds);
            assertEquals(Protocol.TASK_INITIATE, reconstructed.getType());
        }
    }

    @Test
    public void testMultipleSerializationRounds() throws IOException {
        int testRounds = 15;
        TaskInitiate original = new TaskInitiate(testRounds);
        
        // Test multiple marshalling/unmarshalling cycles
        for (int i = 0; i < 10; i++) {
            byte[] data = original.getBytes();
            TaskInitiate reconstructed = new TaskInitiate(data);
            
            assertEquals(testRounds, reconstructed.getRounds(), 
                "Failed on iteration: " + i);
            assertEquals(Protocol.TASK_INITIATE, reconstructed.getType());
        }
    }

    @Test
    public void testMaxIntRounds() throws IOException {
        int maxRounds = Integer.MAX_VALUE;
        
        TaskInitiate original = new TaskInitiate(maxRounds);
        byte[] data = original.getBytes();
        TaskInitiate reconstructed = new TaskInitiate(data);
        
        assertEquals(maxRounds, reconstructed.getRounds());
        assertEquals(Protocol.TASK_INITIATE, reconstructed.getType());
    }

    @Test
    public void testMinIntRounds() throws IOException {
        int minRounds = Integer.MIN_VALUE;
        
        TaskInitiate original = new TaskInitiate(minRounds);
        byte[] data = original.getBytes();
        TaskInitiate reconstructed = new TaskInitiate(data);
        
        assertEquals(minRounds, reconstructed.getRounds());
        assertEquals(Protocol.TASK_INITIATE, reconstructed.getType());
    }

    @Test
    public void testShortDataThrowsException() {
        byte[] shortData = new byte[7]; // Less than 8 bytes needed
        
        assertThrows(IOException.class, () -> {
            new TaskInitiate(shortData);
        });
    }

    @Test
    public void testNullDataThrowsException() {
        assertThrows(IOException.class, () -> {
            new TaskInitiate(null);
        });
    }

    // @Test
    // public void testErrorMessageInException() {
    //     TaskInitiate original = new TaskInitiate(5);
    //     byte[] data = original.getBytes();
        
    //     // Corrupt the message type
    //     data[0] = (byte) 0xFF;
        
    //     Exception exception = assertThrows(IOException.class, () -> {
    //         new TaskInitiate(data);
    //     });
        
    //     assertTrue(exception.getMessage().contains("error"));
    // }
}