package bdgl.jchord;

/**
 * Représente la table de doigts d'un nœud Chord.
 */
public class FingerTable {

    private Finger[] fingers;

    private ChordNode node;
    ChordNode fingerNode = node;

    private int i;
    private ChordKey fingerEnd;

    /**
     * Constructeur de la classe FingerTable.
     * @param node Le nœud pour lequel la table de doigts est créée.
     */
    public FingerTable(ChordNode node) {
        this.node = node;
        this.fingers = new Finger[Hash.KEY_LENGTH];

        // Initialiser les doigts
        for (int i = 0; i < fingers.length; i++) {
            ChordKey start = node.getNodeKey().createStartKey(i);
            if (start != null) {
                fingers[i] = new Finger(start, null, null, null);
            } else {
                // Gérer le cas où la clé de départ est null
                // Par exemple, ignorer ce doigt ou lever une exception selon le cas
                System.err.println("La clé de départ est null pour l'index : " + i);
            }
        }

        // Définir les intervalles de recherche pour chaque doigt
        for (int i = 0; i < fingers.length; i++) {
            if (fingers[i] != null) {
                // Utiliser les doigts seulement si ils ne sont pas null
                ChordKey intervalStart = fingers[i].getStart();
                ChordKey intervalEnd = fingers[(i + 1) % Hash.KEY_LENGTH].getStart();
                ChordNode successor = findSuccessor(node, intervalEnd);
                fingers[i].setIntervalStart(intervalStart);
                fingers[i].setIntervalEnd(intervalEnd);
                fingers[i].setNode(successor);
            }
        }
    }

    /**
     * Obtient le doigt à l'index spécifié.
     * @param i L'index du doigt.
     * @return Le doigt à l'index spécifié.
     */
    public Finger getFinger(int i) {
        return fingers[i];
    }

    /**
     * Réinitialise la table de doigts en la vidant.
     */
    public void clear() {
        // Réinitialise chaque élément du tableau à null
        for (int i = 0; i < fingers.length; i++) {
            fingers[i] = null;
        }
    }

    /**
     * Obtient une entrée de table de doigts pour l'index spécifié.
     * @param i L'index de l'entrée.
     * @return L'entrée de table de doigts pour l'index spécifié.
     */
    public FingerTableEntry get(int i) {
        if (i >= 0 && i < fingers.length) {
            Finger finger = fingers[i];
            if (finger != null) {
                ChordNode node = finger.getNode();
                if (node != null) {
                    ChordNode successor = node.findSuccessor(finger.getIntervalEnd());
                    if (successor != null) {
                        return new FingerTableEntry(finger.getStart(), finger.getIntervalStart(), finger.getIntervalEnd(), successor);
                    } else {
                        // Gérer le cas où aucun successeur n'est trouvé
                        System.err.println("Aucun successeur trouvé pour l'index " + i);
                    }
                }
            }
        } else {
            // Gérer le cas de l'index invalide
            System.err.println("Index invalide : " + i);
        }
        return null; // Retourner null si l'index est invalide ou si le doigt n'a pas de nœud associé
    }




    /**
     * Trouve le successeur pour la clé spécifiée à partir du nœud donné.
     * @param node Le nœud à partir duquel la recherche du successeur est effectuée.
     * @param key La clé pour laquelle le successeur est recherché.
     * @return Le successeur pour la clé spécifiée.
     */
    private ChordNode findSuccessor(ChordNode node, ChordKey key) {
        ChordNode successor = node;
        ChordNode fingerNode = node;

        // Tant que le successeur n'est pas trouvé
        while (true) {
            // Obtenir le successeur du nœud actuel
            ChordNode nextNode = successor != null ? successor.findSuccessor(key) : null;

            // Vérifier si le successeur est null ou s'il est le nœud actuel ou s'il est dans l'intervalle
            if (nextNode != null && (nextNode.equals(fingerNode) || nextNode.getNodeKey().isBetween(fingerNode.getNodeKey(), fingerEnd))) {
                // Le successeur a été trouvé
                successor = nextNode;
                break;
            }

            // Passer au nœud suivant dans l'anneau
            fingerNode = successor;
            successor = successor != null ? successor.getSuccessor() : null;
        }

        return successor;
    }


}
