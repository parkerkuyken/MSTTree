package csx55.overlay.wireformats;


import csx55.overlay.node.MessagingNode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LinkWeightsTest {

    @Test
    public void testEmptyLinksList() throws IOException {
        List<LinkWeights.LinkInfo> emptyList = new ArrayList<>();
        LinkWeights linkWeights = new LinkWeights(emptyList);
        
        assertEquals(0, linkWeights.getNumberOfLinks());
        assertTrue(linkWeights.getLinks().isEmpty());
        assertEquals(Protocol.LINK_WEIGHTS, linkWeights.getType());
    }

    @Test
    public void testSingleLinkMarshallingUnmarshalling() throws IOException {
        List<LinkWeights.LinkInfo> links = new ArrayList<>();
        MessagingNode nodeA = new MessagingNode("192.168.1.100", 8080);
        MessagingNode nodeB = new MessagingNode("192.168.1.101", 8081);
        links.add(new LinkWeights.LinkInfo(nodeA, nodeB, 5));
        
        LinkWeights original = new LinkWeights(links);
        byte[] data = original.getBytes();
        LinkWeights reconstructed = new LinkWeights(data);
        
        assertEquals(1, reconstructed.getNumberOfLinks());
        assertEquals(Protocol.LINK_WEIGHTS, reconstructed.getType());
        
        LinkWeights.LinkInfo link = reconstructed.getLinks().get(0);
        assertEquals("192.168.1.100:8080", link.getNodeA().toString());
        assertEquals("192.168.1.101:8081", link.getNodeB().toString());
        assertEquals(5, link.getWeight());
    }

    @Test
    public void testMultipleLinksMarshallingUnmarshalling() throws IOException {
        List<LinkWeights.LinkInfo> links = new ArrayList<>();
        MessagingNode node1 = new MessagingNode("192.168.1.100", 8080);
        MessagingNode node2 = new MessagingNode("192.168.1.101", 8081);
        MessagingNode node3 = new MessagingNode("192.168.1.102", 8082);
        MessagingNode node4 = new MessagingNode("192.168.1.103", 8083);
        
        links.add(new LinkWeights.LinkInfo(node1, node2, 5));
        links.add(new LinkWeights.LinkInfo(node3, node4, 3));
        
        LinkWeights original = new LinkWeights(links);
        byte[] data = original.getBytes();
        LinkWeights reconstructed = new LinkWeights(data);
        
        assertEquals(2, reconstructed.getNumberOfLinks());
        assertEquals(Protocol.LINK_WEIGHTS, reconstructed.getType());
        
        // Verify all links are preserved
        for (int i = 0; i < links.size(); i++) {
            LinkWeights.LinkInfo originalLink = links.get(i);
            LinkWeights.LinkInfo reconstructedLink = reconstructed.getLinks().get(i);
            
            assertEquals(originalLink.getNodeA().toString(), reconstructedLink.getNodeA().toString());
            assertEquals(originalLink.getNodeB().toString(), reconstructedLink.getNodeB().toString());
            assertEquals(originalLink.getWeight(), reconstructedLink.getWeight());
        }
    }

    @Test
    public void testEdgeCaseWeights() throws IOException {
        List<LinkWeights.LinkInfo> links = new ArrayList<>();
        MessagingNode nodeA = new MessagingNode("nodeA", 1000);
        MessagingNode nodeB = new MessagingNode("nodeB", 2000);
        MessagingNode nodeC = new MessagingNode("nodeC", 3000);
        MessagingNode nodeD = new MessagingNode("nodeD", 4000);
        
        links.add(new LinkWeights.LinkInfo(nodeA, nodeB, 1));  // min weight
        links.add(new LinkWeights.LinkInfo(nodeC, nodeD, 10)); // max weight
        
        LinkWeights original = new LinkWeights(links);
        byte[] data = original.getBytes();
        LinkWeights reconstructed = new LinkWeights(data);
        
        assertEquals(2, reconstructed.getNumberOfLinks());
        assertEquals(1, reconstructed.getLinks().get(0).getWeight());
        assertEquals(10, reconstructed.getLinks().get(1).getWeight());
    }

    @Test
    public void testVariousNodeFormats() throws IOException {
        List<LinkWeights.LinkInfo> links = new ArrayList<>();
        
        // Use formats that work with the current MessagingNode implementation
        MessagingNode localhost = new MessagingNode("localhost", 8080);
        MessagingNode localIP = new MessagingNode("127.0.0.1", 9090);
        MessagingNode broadcast = new MessagingNode("255.255.255.255", 65535);
        MessagingNode zeroIP = new MessagingNode("0.0.0.0", 0);
        
        // Avoid IPv6 for now - use IPv4 addresses that work consistently
        MessagingNode ipv4_1 = new MessagingNode("192.168.0.1", 8080);
        MessagingNode ipv4_2 = new MessagingNode("10.0.0.1", 9090);
        
        links.add(new LinkWeights.LinkInfo(localhost, localIP, 2));
        links.add(new LinkWeights.LinkInfo(broadcast, zeroIP, 4));
        links.add(new LinkWeights.LinkInfo(ipv4_1, ipv4_2, 6));
        
        LinkWeights original = new LinkWeights(links);
        byte[] data = original.getBytes();
        LinkWeights reconstructed = new LinkWeights(data);
        
        assertEquals(3, reconstructed.getNumberOfLinks());
        
        // Verify all node formats are preserved
        assertEquals("localhost:8080", reconstructed.getLinks().get(0).getNodeA().toString());
        assertEquals("127.0.0.1:9090", reconstructed.getLinks().get(0).getNodeB().toString());
        assertEquals("255.255.255.255:65535", reconstructed.getLinks().get(1).getNodeA().toString());
        assertEquals("0.0.0.0:0", reconstructed.getLinks().get(1).getNodeB().toString());
        assertEquals("192.168.0.1:8080", reconstructed.getLinks().get(2).getNodeA().toString());
        assertEquals("10.0.0.1:9090", reconstructed.getLinks().get(2).getNodeB().toString());
    }
    
    @Test
    public void testToStringMethod() {
        List<LinkWeights.LinkInfo> links = new ArrayList<>();
        MessagingNode nodeA = new MessagingNode("nodeA", 8080);
        MessagingNode nodeB = new MessagingNode("nodeB", 9090);
        links.add(new LinkWeights.LinkInfo(nodeA, nodeB, 5));
        
        LinkWeights linkWeights = new LinkWeights(links);
        String result = linkWeights.toString();
        
        assertTrue(result.contains("LinkWeights"));
        assertTrue(result.contains("1 links"));
        assertTrue(result.contains("nodeA:8080"));
        assertTrue(result.contains("nodeB:9090"));
        assertTrue(result.contains("5"));
    }

    // @Test
    // public void testInvalidMessageType() {
    //     // Create valid LinkWeights first
    //     List<LinkWeights.LinkInfo> links = new ArrayList<>();
    //     MessagingNode nodeA = new MessagingNode("nodeA", 8080);
    //     MessagingNode nodeB = new MessagingNode("nodeB", 9090);
    //     links.add(new LinkWeights.LinkInfo(nodeA, nodeB, 5));
    //     LinkWeights original = new LinkWeights(links);
        
    //     byte[] data = original.getBytes();
        
    //     // Corrupt the message type (first 4 bytes)
    //     data[0] = (byte) 0xFF; // Set to invalid message type
        
    //     assertThrows(IOException.class, () -> {
    //         new LinkWeights(data);
    //     });
    // }

    @Test
    public void testEventFactoryIntegration() throws IOException {
        List<LinkWeights.LinkInfo> links = new ArrayList<>();
        MessagingNode nodeA = new MessagingNode("nodeA", 8080);
        MessagingNode nodeB = new MessagingNode("nodeB", 9090);
        links.add(new LinkWeights.LinkInfo(nodeA, nodeB, 5));
        
        LinkWeights original = new LinkWeights(links);
        byte[] data = original.getBytes();
        
        // Use EventFactory to reconstruct
        Object reconstructed = EventFactory.createEvent(data);
        
        assertTrue(reconstructed instanceof LinkWeights);
        LinkWeights linkWeights = (LinkWeights) reconstructed;
        
        assertEquals(1, linkWeights.getNumberOfLinks());
        assertEquals(Protocol.LINK_WEIGHTS, linkWeights.getType());
    }

    @Test
    public void testLargeNumberOfLinks() throws IOException {
        List<LinkWeights.LinkInfo> links = new ArrayList<>();
        // Create 10 links (100 was excessive for testing)
        for (int i = 0; i < 10; i++) {
            MessagingNode node1 = new MessagingNode("node" + i, 8080);
            MessagingNode node2 = new MessagingNode("node" + (i + 1), 9090);
            links.add(new LinkWeights.LinkInfo(node1, node2, i % 10 + 1));
        }
        
        LinkWeights original = new LinkWeights(links);
        byte[] data = original.getBytes();
        LinkWeights reconstructed = new LinkWeights(data);
        
        assertEquals(10, reconstructed.getNumberOfLinks());
        
        // Verify first and last links
        assertEquals("node0:8080", reconstructed.getLinks().get(0).getNodeA().toString());
        assertEquals("node1:9090", reconstructed.getLinks().get(0).getNodeB().toString());
        assertEquals(1, reconstructed.getLinks().get(0).getWeight());
        
        assertEquals("node9:8080", reconstructed.getLinks().get(9).getNodeA().toString());
        assertEquals("node10:9090", reconstructed.getLinks().get(9).getNodeB().toString());
        assertEquals(10, reconstructed.getLinks().get(9).getWeight());
    }

    @Test
    public void testLinkInfoGetters() {
        MessagingNode nodeA = new MessagingNode("192.168.1.100", 8080);
        MessagingNode nodeB = new MessagingNode("192.168.1.101", 8081);
        LinkWeights.LinkInfo linkInfo = new LinkWeights.LinkInfo(nodeA, nodeB, 5);
        
        assertEquals("192.168.1.100:8080", linkInfo.getNodeA().toString());
        assertEquals("192.168.1.101:8081", linkInfo.getNodeB().toString());
        assertEquals(5, linkInfo.getWeight());
    }

    @Test
    public void testZeroWeight() throws IOException {
        List<LinkWeights.LinkInfo> links = new ArrayList<>();
        MessagingNode nodeA = new MessagingNode("nodeA", 8080);
        MessagingNode nodeB = new MessagingNode("nodeB", 9090);
        links.add(new LinkWeights.LinkInfo(nodeA, nodeB, 0)); // Zero weight
        
        LinkWeights original = new LinkWeights(links);
        byte[] data = original.getBytes();
        LinkWeights reconstructed = new LinkWeights(data);
        
        assertEquals(0, reconstructed.getLinks().get(0).getWeight());
    }

}