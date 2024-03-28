package kmaru.jchord;

/**
 * Représente un doigt dans la table de doigts d'un nœud Chord.
 */
public class Finger {
    private ChordKey start;
    private ChordNode node;
    private ChordKey intervalStart;
    private ChordKey intervalEnd;

    /**
     * Crée une nouvelle instance de Finger avec les clés de départ, de début d'intervalle, de fin d'intervalle et le nœud associé.
     * @param start La clé de départ du doigt.
     * @param intervalStart La clé de début d'intervalle du doigt.
     * @param intervalEnd La clé de fin d'intervalle du doigt.
     * @param node Le nœud associé au doigt.
     */
    public Finger(ChordKey start, ChordKey intervalStart, ChordKey intervalEnd, ChordNode node) {
        this.start = start;
        this.node = node;
        this.intervalStart = intervalStart;
        this.intervalEnd = intervalEnd;
    }

    public ChordKey getStart() {
        return start;
    }

    public void setStart(ChordKey start) {
        this.start = start;
    }

    public ChordNode getNode() {
        return node;
    }

    public void setNode(ChordNode node) {
        this.node = node;
    }

    public ChordKey getIntervalStart() {
        return intervalStart;
    }

    public ChordKey getIntervalEnd() {
        // Pour obtenir l'intervalle de fin du doigt, nous devons calculer la clé de départ du doigt suivant
        // et déplacer cette clé d'un pas vers la gauche en utilisant la méthode chord.Predecessor()
        // pour obtenir l'intervalle de fin du doigt actuel.
        ChordKey nextStart = start.createNextKey();
        ChordNode predecessor = node.getPredecessor();
        if (predecessor != null) {
            return predecessor.getNodeKey();
        }
        return nextStart;
    }

    public void setIntervalStart(ChordKey intervalStart) {
        this.intervalStart = intervalStart;
    }

    public void setIntervalEnd(ChordKey intervalEnd) {
        this.intervalEnd = intervalEnd;
    }
}
