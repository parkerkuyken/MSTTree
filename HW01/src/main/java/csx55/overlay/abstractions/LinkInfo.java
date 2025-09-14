package csx55.overlay.abstractions;


public class LinkInfo{


    private NodeInfo node1;
    private NodeInfo node2;
    private int linkValues;
    
    public LinkInfo(NodeInfo node1, NodeInfo node2, int linkValues){
        this.node1 = node1;
        this.node2 = node2;
        this.linkValues = linkValues;
    }

    public synchronized NodeInfo getNode1() {
        return node1;
    }
    public synchronized NodeInfo getNode2() {
        return node2;
    }
    public synchronized int getLinkValues() {
        return linkValues;

    }
}