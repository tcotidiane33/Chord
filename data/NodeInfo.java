package kmaru.jchord.test;


public class NodeInfo {
    private String nodeId;
    private long numBytesStored;

    public NodeInfo(String nodeId, long numBytesStored) {
        this.nodeId = nodeId;
        this.numBytesStored = numBytesStored;
    }

    public String getNodeId() {
        return nodeId;
    }

    public long getNumBytesStored() {
        return numBytesStored;
    }
}