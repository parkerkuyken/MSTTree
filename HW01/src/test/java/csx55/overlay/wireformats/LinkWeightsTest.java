package csx55.overlay.test;

import csx55.overlay.wireformats.EventFactory;
import csx55.overlay.wireformats.LinkWeights;
import csx55.overlay.wireformats.Protocol;
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
        links.add(new LinkWeights.LinkInfo("192.168.1.100:8080", "192.168.1.101:8081", 5));
        
        LinkWeights original = new LinkWeights(links);
        byte[] data = original.getBytes();
        LinkWeights reconstructed = new LinkWeights(data);
        
        assertEquals(1, reconstructed.getNumberOfLinks());
        assertEquals(Protocol.LINK_WEIGHTS, reconstructed.getType());
        
        LinkWeights.LinkInfo link = reconstructed.getLinks().get(0);
        assertEquals("192.168.1.100:8080", link.getNodeA());
        assertEquals("192.168.1.101:8081", link.getNodeB());
        assertEquals(5, link.getWeight());
    }

    @Test
    public void testMultipleLinksMarshallingUnmarshalling() throws IOException {
        List<LinkWeights.LinkInfo> links = new ArrayList<>();
        links.add(new LinkWeights.LinkInfo("192.168.1.100:8080", "192.168.1.101:8081", 5));
        links.add(new LinkWeights.LinkInfo("192.168.1.102:8082", "192.168.1.103:8083", 3));
        links.add(new LinkWeights.LinkInfo("192.168.1.104:8084", "192.168.1.105:8085", 7));
        
        LinkWeights original = new LinkWeights(links);
        byte[] data = original.getBytes();
        LinkWeights reconstructed = new LinkWeights(data);
        
        assertEquals(3, reconstructed.getNumberOfLinks());
        assertEquals(Protocol.LINK_WEIGHTS, reconstructed.getType());
        
        // Verify all links are preserved
        for (int i = 0; i < links.size(); i++) {
            LinkWeights.LinkInfo originalLink = links.get(i);
            LinkWeights.LinkInfo reconstructedLink = reconstructed.getLinks().get(i);
            
            assertEquals(originalLink.getNodeA(), reconstructedLink.getNodeA());
            assertEquals(originalLink.getNodeB(), reconstructedLink.getNodeB());
            assertEquals(originalLink.getWeight(), reconstructedLink.getWeight());
        }
    }

    @Test
    public void testEdgeCaseWeights() throws IOException {
        List<LinkWeights.LinkInfo> links = new ArrayList<>();
        links.add(new LinkWeights.LinkInfo("nodeA:1000", "nodeB:2000", 1));  // min weight
        links.add(new LinkWeights.LinkInfo("nodeC:3000", "nodeD:4000", 10)); // max weight
        
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
        links.add(new LinkWeights.LinkInfo("localhost:8080", "127.0.0.1:9090", 2));
        links.add(new LinkWeights.LinkInfo("255.255.255.255:65535", "0.0.0.0:0", 4));
        links.add(new LinkWeights.LinkInfo("::1:8080", "2001:db8::1:9090", 6)); // IPv6
        
        LinkWeights original = new LinkWeights(links);
        byte[] data = original.getBytes();
        LinkWeights reconstructed = new LinkWeights(data);
        
        assertEquals(3, reconstructed.getNumberOfLinks());
        
        // Verify all node formats are preserved
        assertEquals("localhost:8080", reconstructed.getLinks().get(0).getNodeA());
        assertEquals("127.0.0.1:9090", reconstructed.getLinks().get(0).getNodeB());
        assertEquals("255.255.255.255:65535", reconstructed.getLinks().get(1).getNodeA());
        assertEquals("0.0.0.0:0", reconstructed.getLinks().get(1).getNodeB());
        assertEquals("::1:8080", reconstructed.getLinks().get(2).getNodeA());
        assertEquals("2001:db8::1:9090", reconstructed.getLinks().get(2).getNodeB());
    }

    @Test
    public void testToStringMethod() {
        List<LinkWeights.LinkInfo> links = new ArrayList<>();
        links.add(new LinkWeights.LinkInfo("nodeA:8080", "nodeB:9090", 5));
        
        LinkWeights linkWeights = new LinkWeights(links);
        String result = linkWeights.toString();
        System.out.println(result);
        
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
    //     links.add(new LinkWeights.LinkInfo("nodeA:8080", "nodeB:9090", 5));
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
        links.add(new LinkWeights.LinkInfo("nodeA:8080", "nodeB:9090", 5));
        
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
        // Create 100 links
        for (int i = 0; i < 100; i++) {
            links.add(new LinkWeights.LinkInfo("node" + i + ":8080", "node" + (i + 1) + ":9090", i % 10 + 1));
        }
        
        LinkWeights original = new LinkWeights(links);
        byte[] data = original.getBytes();
        LinkWeights reconstructed = new LinkWeights(data);
        
        assertEquals(100, reconstructed.getNumberOfLinks());
        
        // Verify a few random links
        assertEquals("node0:8080", reconstructed.getLinks().get(0).getNodeA());
        assertEquals("node1:9090", reconstructed.getLinks().get(0).getNodeB());
        assertEquals(1, reconstructed.getLinks().get(0).getWeight());
        
        assertEquals("node99:8080", reconstructed.getLinks().get(99).getNodeA());
        assertEquals("node100:9090", reconstructed.getLinks().get(99).getNodeB());
        assertEquals(10, reconstructed.getLinks().get(99).getWeight());
    }

    @Test
    public void testLinkInfoGetters() {
        LinkWeights.LinkInfo linkInfo = new LinkWeights.LinkInfo("192.168.1.100:8080", "192.168.1.101:8081", 5);
        
        assertEquals("192.168.1.100:8080", linkInfo.getNodeA());
        assertEquals("192.168.1.101:8081", linkInfo.getNodeB());
        assertEquals(5, linkInfo.getWeight());
    }

    @Test
    public void testZeroWeight() throws IOException {
        List<LinkWeights.LinkInfo> links = new ArrayList<>();
        links.add(new LinkWeights.LinkInfo("nodeA:8080", "nodeB:9090", 0)); // Zero weight
        
        LinkWeights original = new LinkWeights(links);
        byte[] data = original.getBytes();
        LinkWeights reconstructed = new LinkWeights(data);
        
        assertEquals(0, reconstructed.getLinks().get(0).getWeight());
    }
}