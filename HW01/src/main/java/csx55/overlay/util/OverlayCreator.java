package csx55.overlay.util;

import java.util.concurrent.ConcurrentMap;

import csx55.overlay.abstractions.NodeInfo;

public class OverlayCreator{

    private final ConcurrentMap<String, NodeInfo> nodeRegistry;
    
    public OverlayCreator(ConcurrentMap<String, NodeInfo> nodeRegistry){ 
        this.nodeRegistry = nodeRegistry;
    }

    /**
     * Creates connections in a circular manner
     * @param numConnections Number of connections each node should have
     */
    public void createOverlay(int numConnections) {
        String[] nodes = nodeRegistry.keySet().toArray(new String[0]);
        int totalNodes = nodes.length;
    
        //Algorithm to create a circular overlay
        for (int i = 0; i < totalNodes; i++) {
            NodeInfo node1 = nodeRegistry.get(nodes[i]);
            int half = numConnections / 2;
            if (numConnections % 2 != 0) {
                half += 1; // If odd, connect to one extra node on one side
            }
        //This ensures that each node connects to half the nodes on its left and half on its right
            for (int j = 1; j <= half; j++) {
                int rightNode = (i + j) % totalNodes;
                int leftNode = (i - j + totalNodes) % totalNodes;
        
                // Add connections both ways to ensure bidirectional links
                NodeInfo rightNode2 = nodeRegistry.get(nodes[rightNode]);
                NodeInfo leftNode2 = nodeRegistry.get(nodes[leftNode]);

                node1.addConnection(rightNode2.getKey());
                rightNode2.addConnection(node1.getKey());
        
                node1.addConnection(leftNode2.getKey());
                leftNode2.addConnection(node1.getKey());
            }
        }
        
    }
    
}