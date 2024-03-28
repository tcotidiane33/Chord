package kmaru.jchord;

public class FingerTableEntry {
    private ChordKey start;
    private ChordNode node;

    private ChordKey intervalStart;
    private ChordKey intervalEnd;
    private ChordNode successor;

    public FingerTableEntry(ChordKey start, ChordNode node) {
        this.start = start;
        this.node = node;
    }

    public FingerTableEntry(ChordKey start, ChordKey intervalStart, ChordKey intervalEnd, ChordNode successor) {
        this.start = start;
        this.intervalStart = intervalStart;
        this.intervalEnd = intervalEnd;
        this.successor = successor;
    }

    public ChordKey getStart() {
        return start;
    }

    public ChordNode getNode() {
        return node;
    }

    public void setNode(ChordNode node) {
        this.node = node;
    }
}
