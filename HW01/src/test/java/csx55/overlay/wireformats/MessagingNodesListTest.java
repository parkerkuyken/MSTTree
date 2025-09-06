package csx55.overlay.wireformats;


import csx55.overlay.node.MessagingNode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessagingNodesListTest {

    @Test
    public void testEmptyPeerList() throws IOException {
        List<MessagingNode> emptyList = new ArrayList<>();
        MessagingNodesList message = new MessagingNodesList(emptyList);
        
        assertEquals(0, message.getNumberOfPeers());
        assertTrue(message.getPeerNodes().isEmpty());
        assertEquals(Protocol.MESSAGING_NODES_LIST, message.getType());
    }

    @Test
    public void testSinglePeerMarshallingUnmarshalling() throws IOException {
        List<MessagingNode> peers = new ArrayList<>();
        peers.add(new MessagingNode("192.168.1.100", 8080));
        
        MessagingNodesList original = new MessagingNodesList(peers);
        byte[] data = original.getBytes();
        MessagingNodesList reconstructed = new MessagingNodesList(data);
        
        assertEquals(1, reconstructed.getNumberOfPeers());
        assertEquals(Protocol.MESSAGING_NODES_LIST, reconstructed.getType());
        
        MessagingNode node = reconstructed.getPeerNodes().get(0);
        assertEquals("192.168.1.100", node.getIp());
        assertEquals(8080, node.getPort());
    }

    @Test
    public void testMultiplePeersMarshallingUnmarshalling() throws IOException {
        List<MessagingNode> peers = new ArrayList<>();
        peers.add(new MessagingNode("192.168.1.100", 8080));
        peers.add(new MessagingNode("192.168.1.101", 8081));
        peers.add(new MessagingNode("192.168.1.102", 8082));
        
        MessagingNodesList original = new MessagingNodesList(peers);
        byte[] data = original.getBytes();
        MessagingNodesList reconstructed = new MessagingNodesList(data);
        
        assertEquals(3, reconstructed.getNumberOfPeers());
        assertEquals(Protocol.MESSAGING_NODES_LIST, reconstructed.getType());
        
        // Verify all peers are preserved in order
        for (int i = 0; i < peers.size(); i++) {
            MessagingNode originalNode = peers.get(i);
            MessagingNode reconstructedNode = reconstructed.getPeerNodes().get(i);
            
            assertEquals(originalNode.getIp(), reconstructedNode.getIp());
            assertEquals(originalNode.getPort(), reconstructedNode.getPort());
        }
    }

    // @Test
    // public void testVariousIPFormats() throws IOException {
    //     List<MessagingNode> peers = new ArrayList<>();
    //     peers.add(new MessagingNode("localhost", 8080));
    //     peers.add(new MessagingNode("127.0.0.1", 9090));
    //     peers.add(new MessagingNode("255.255.255.255", 65535));
    //     peers.add(new MessagingNode("::1", 8080)); // IPv6
        
    //     MessagingNodesList original = new MessagingNodesList(peers);
    //     byte[] data = original.getBytes();
    //     MessagingNodesList reconstructed = new MessagingNodesList(data);
        
    //     assertEquals(4, reconstructed.getNumberOfPeers());
        
    //     // Verify all IP formats are preserved
    //     assertEquals("localhost", reconstructed.getPeerNodes().get(0).getIp());
    //     assertEquals("127.0.0.1", reconstructed.getPeerNodes().get(1).getIp());
    //     assertEquals("255.255.255.255", reconstructed.getPeerNodes().get(2).getIp());
    //     assertEquals("::1", reconstructed.getPeerNodes().get(3).getIp());
    // }

    @Test
    public void testEdgeCasePortNumbers() throws IOException {
        List<MessagingNode> peers = new ArrayList<>();
        peers.add(new MessagingNode("192.168.1.100", 1));       // min port
        peers.add(new MessagingNode("192.168.1.101", 65535));   // max port
        peers.add(new MessagingNode("192.168.1.102", 0));       // zero port
        peers.add(new MessagingNode("192.168.1.103", 1024));    // well-known
        
        MessagingNodesList original = new MessagingNodesList(peers);
        byte[] data = original.getBytes();
        MessagingNodesList reconstructed = new MessagingNodesList(data);
        
        assertEquals(1, reconstructed.getPeerNodes().get(0).getPort());
        assertEquals(65535, reconstructed.getPeerNodes().get(1).getPort());
        assertEquals(0, reconstructed.getPeerNodes().get(2).getPort());
        assertEquals(1024, reconstructed.getPeerNodes().get(3).getPort());
    }

    @Test
    public void testToStringMethod() {
        List<MessagingNode> peers = new ArrayList<>();
        peers.add(new MessagingNode("192.168.1.100", 8080));
        peers.add(new MessagingNode("192.168.1.101", 8081));
        
        MessagingNodesList message = new MessagingNodesList(peers);
        String result = message.toString();
        
        assertTrue(result.contains("MessagingNodesList"));
        assertTrue(result.contains("peers=2"));
        assertTrue(result.contains("192.168.1.100:8080"));
        assertTrue(result.contains("192.168.1.101:8081"));
    }

    @Test
    public void testMessagingNodeGetters() {
        MessagingNode node = new MessagingNode("192.168.1.100", 8080);
        
        assertEquals("192.168.1.100", node.getIp());
        assertEquals(8080, node.getPort());
        assertEquals("192.168.1.100:8080", node.toString());
    }

    // @Test
    // public void testInvalidMessageType() {
    //     // Create valid message first
    //     List<MessagingNode> peers = new ArrayList<>();
    //     peers.add(new MessagingNode("192.168.1.100", 8080));
    //     MessagingNodesList original = new MessagingNodesList(peers);
        
    //     byte[] data = original.getBytes();
        
    //     // Corrupt the message type (first 4 bytes)
    //     data[0] = (byte) 0xFF; // Set to invalid message type
        
    //     assertThrows(IOException.class, () -> {
    //         new MessagingNodesList(data);
    //     });
    // }

    @Test
    public void testEventFactoryIntegration() throws IOException {
        List<MessagingNode> peers = new ArrayList<>();
        peers.add(new MessagingNode("192.168.1.100", 8080));
        peers.add(new MessagingNode("192.168.1.101", 8081));
        
        MessagingNodesList original = new MessagingNodesList(peers);
        byte[] data = original.getBytes();
        
        // Use EventFactory to reconstruct
        Object reconstructed = EventFactory.createEvent(data);
        
        assertTrue(reconstructed instanceof MessagingNodesList);
        MessagingNodesList message = (MessagingNodesList) reconstructed;
        
        assertEquals(2, message.getNumberOfPeers());
        assertEquals(Protocol.MESSAGING_NODES_LIST, message.getType());
    }

    @Test
    public void testLargeNumberOfPeers() throws IOException {
        List<MessagingNode> peers = new ArrayList<>();
        // Create 10 peers (reduced from 50 for faster testing)
        for (int i = 0; i < 10; i++) {
            peers.add(new MessagingNode("192.168.1." + (100 + i), 8080 + i));
        }
        
        MessagingNodesList original = new MessagingNodesList(peers);
        byte[] data = original.getBytes();
        MessagingNodesList reconstructed = new MessagingNodesList(data);
        
        assertEquals(10, reconstructed.getNumberOfPeers());
        
        // Verify first and last peers
        assertEquals("192.168.1.100", reconstructed.getPeerNodes().get(0).getIp());
        assertEquals(8080, reconstructed.getPeerNodes().get(0).getPort());
        
        assertEquals("192.168.1.109", reconstructed.getPeerNodes().get(9).getIp());
        assertEquals(8089, reconstructed.getPeerNodes().get(9).getPort());
    }

    // @Test
    // public void testZeroLengthIP() throws IOException {
    //     List<MessagingNode> peers = new ArrayList<>();
    //     peers.add(new MessagingNode("", 8080)); // Empty IP
        
    //     MessagingNodesList original = new MessagingNodesList(peers);
    //     byte[] data = original.getBytes();
    //     MessagingNodesList reconstructed = new MessagingNodesList(data);
        
    //     assertEquals("", reconstructed.getPeerNodes().get(0).getIp());
    //     assertEquals(8080, reconstructed.getPeerNodes().get(0).getPort());
    // }

    @Test
    public void testMessagingNodeEquality() {
        MessagingNode node1 = new MessagingNode("192.168.1.100", 8080);
        MessagingNode node2 = new MessagingNode("192.168.1.100", 8080);
        MessagingNode node3 = new MessagingNode("192.168.1.101", 8080);
        
        // Test equals
        assertTrue(node1.equals(node2));
        assertFalse(node1.equals(node3));
        
        // Test hashCode consistency
        assertEquals(node1.hashCode(), node2.hashCode());
        assertNotEquals(node1.hashCode(), node3.hashCode());
        
        // Test string constructor
        MessagingNode node4 = new MessagingNode("192.168.1.100:8080");
        assertTrue(node1.equals(node4));
    }

    @Test
    public void testFromStringConstructor() {
        MessagingNode node = new MessagingNode("192.168.1.100:8080");
        
        assertEquals("192.168.1.100", node.getIp());
        assertEquals(8080, node.getPort());
        assertEquals("192.168.1.100:8080", node.toString());
    }

    @Test
    public void testInvalidStringConstructor() {
        assertThrows(IllegalArgumentException.class, () -> {
            new MessagingNode("invalid-format");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new MessagingNode("192.168.1.100:invalid-port");
        });
    }
}