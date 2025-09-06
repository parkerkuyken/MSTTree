package csx55.overlay.wireformats;


import csx55.overlay.node.MessagingNode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

public class TaskSummaryResponseTest {

    @Test
    public void testMarshallingUnmarshalling() throws IOException {
        MessagingNode node = new MessagingNode("192.168.1.100", 8080);
        TaskSummaryResponse original = new TaskSummaryResponse(
            node, 100, 5000L, 80, 4000L, 30
        );
        
        byte[] data = original.getBytes();
        TaskSummaryResponse reconstructed = new TaskSummaryResponse(data);
        
        assertEquals(Protocol.TRAFFIC_SUMMARY, reconstructed.getType());
        assertEquals("192.168.1.100:8080", reconstructed.getNode().toString());
        assertEquals(100, reconstructed.getMessagesSent());
        assertEquals(5000L, reconstructed.getSendSummation());
        assertEquals(80, reconstructed.getMessagesReceived());
        assertEquals(4000L, reconstructed.getReceiveSummation());
        assertEquals(30, reconstructed.getMessagesRelayed());
    }

    @Test
    public void testEdgeCaseValues() throws IOException {
        MessagingNode node = new MessagingNode("localhost", 8080);
        TaskSummaryResponse original = new TaskSummaryResponse(
            node, 0, 0L, 0, 0L, 0
        );
        
        byte[] data = original.getBytes();
        TaskSummaryResponse reconstructed = new TaskSummaryResponse(data);
        
        assertEquals(0, reconstructed.getMessagesSent());
        assertEquals(0L, reconstructed.getSendSummation());
        assertEquals(0, reconstructed.getMessagesReceived());
        assertEquals(0L, reconstructed.getReceiveSummation());
        assertEquals(0, reconstructed.getMessagesRelayed());
    }

    @Test
    public void testLargeValues() throws IOException {
        MessagingNode node = new MessagingNode("10.0.0.1", 9090);
        TaskSummaryResponse original = new TaskSummaryResponse(
            node, 
            Integer.MAX_VALUE, 
            Long.MAX_VALUE, 
            Integer.MAX_VALUE, 
            Long.MAX_VALUE, 
            Integer.MAX_VALUE
        );
        
        byte[] data = original.getBytes();
        TaskSummaryResponse reconstructed = new TaskSummaryResponse(data);
        
        assertEquals(Integer.MAX_VALUE, reconstructed.getMessagesSent());
        assertEquals(Long.MAX_VALUE, reconstructed.getSendSummation());
        assertEquals(Integer.MAX_VALUE, reconstructed.getMessagesReceived());
        assertEquals(Long.MAX_VALUE, reconstructed.getReceiveSummation());
        assertEquals(Integer.MAX_VALUE, reconstructed.getMessagesRelayed());
    }

    @Test
    public void testNegativeValues() throws IOException {
        MessagingNode node = new MessagingNode("127.0.0.1", 8080);
        TaskSummaryResponse original = new TaskSummaryResponse(
            node, 
            -100, 
            -5000L, 
            -80, 
            -4000L, 
            -30
        );
        
        byte[] data = original.getBytes();
        TaskSummaryResponse reconstructed = new TaskSummaryResponse(data);
        
        assertEquals(-100, reconstructed.getMessagesSent());
        assertEquals(-5000L, reconstructed.getSendSummation());
        assertEquals(-80, reconstructed.getMessagesReceived());
        assertEquals(-4000L, reconstructed.getReceiveSummation());
        assertEquals(-30, reconstructed.getMessagesRelayed());
    }

 


    @Test
    public void testEventFactoryIntegration() throws IOException {
        MessagingNode node = new MessagingNode("192.168.1.100", 8080);
        TaskSummaryResponse original = new TaskSummaryResponse(node, 100, 5000L, 80, 4000L, 30);
        
        byte[] data = original.getBytes();
        
        Object reconstructed = EventFactory.createEvent(data);
        assertTrue(reconstructed instanceof TaskSummaryResponse);
        
        TaskSummaryResponse response = (TaskSummaryResponse) reconstructed;
        assertEquals("192.168.1.100:8080", response.getNode().toString());
        assertEquals(100, response.getMessagesSent());
    }

    @Test
    public void testGetters() {
        MessagingNode node = new MessagingNode("192.168.1.100", 8080);
        TaskSummaryResponse response = new TaskSummaryResponse(node, 100, 5000L, 80, 4000L, 30);
        
        assertEquals("192.168.1.100:8080", response.getNode().toString());
        assertEquals(100, response.getMessagesSent());
        assertEquals(5000L, response.getSendSummation());
        assertEquals(80, response.getMessagesReceived());
        assertEquals(4000L, response.getReceiveSummation());
        assertEquals(30, response.getMessagesRelayed());
    }

    @Test
    public void testShortData() {
        byte[] shortData = new byte[10]; // Too short for complete message
        assertThrows(IOException.class, () -> {
            new TaskSummaryResponse(shortData);
        });
    }

    @Test
    public void testNullData() {
        assertThrows(IOException.class, () -> {
            new TaskSummaryResponse(null);
        });
    }

}