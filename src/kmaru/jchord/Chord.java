package kmaru.jchord;

import java.util.*;

public class Chord {
    private List<ChordNode> nodeList = new ArrayList<>();
    private SortedMap<ChordKey, ChordNode> sortedNodeMap = new TreeMap<>();
    private int numOfNodes;

    public Chord() {
        this.numOfNodes = numOfNodes;
    }

    public void createNode(String nodeId) throws ChordException {
        ChordNode node = new ChordNode(nodeId);
        if (sortedNodeMap.containsKey(node.getNodeKey())) {
            throw new ChordException("Duplicate Key: " + nodeId);
        }
        sortedNodeMap.put(node.getNodeKey(), node);
        nodeList.add(node);
    }

    public ChordNode getNode(int index) {
        return index >= 0 && index < nodeList.size() ? nodeList.get(index) : null;
    }

    public ChordNode getSortedNode(int index) {
        if (index >= 0 && index < sortedNodeMap.size()) {
            return new ArrayList<>(sortedNodeMap.values()).get(index);
        }
        return null;
    }

    public ChordNode findSuccessor(String key) {
        return findSuccessor(new ChordKey(key));
    }

    public ChordNode findSuccessor(ChordKey key) {
        if (sortedNodeMap.isEmpty()) {
            return null;
        }

        // Récupérer la première clé du tailMap
        ChordKey firstKey = sortedNodeMap.tailMap(key).firstKey();

        // Récupérer le nœud associé à la première clé
        ChordNode successor = sortedNodeMap.getOrDefault(firstKey, sortedNodeMap.get(sortedNodeMap.firstKey()));

        return successor;
    }


    public int size() {
        return numOfNodes;
    }

    public void dropNode(int index) {
        if (index >= 0 && index < nodeList.size()) {
            ChordNode node = nodeList.remove(index);
            sortedNodeMap.remove(node.getNodeKey());
            System.out.println("Node at index " + index + " removed successfully.");
        } else {
            System.out.println("Invalid node index.");
        }
    }

    /**
     * Supprime un nœud de l'anneau Chord.
     * @param node Le nœud à supprimer.
     */
    public void removeNode(ChordNode node) {
        // Mettre à jour le successeur du prédécesseur du nœud à supprimer
        ChordNode predecessor = node.getPredecessor();
        ChordNode successor = node.getSuccessor();
        if (predecessor != null) {
            predecessor.setSuccessor(successor);
        }
        // Mettre à jour le prédécesseur du successeur du nœud à supprimer
        if (successor != null) {
            successor.setPredecessor(predecessor);
        }
        // Réorganiser la table de hachage distribuée des clés si nécessaire
        // Cela dépend de votre implémentation spécifique et des règles de redistribution des clés
        // dans le cas de la suppression d'un nœud de l'anneau Chord.
    }

    /**
     * Ajoute une paire clé-valeur de données à l'anneau Chord.
     * @param key La clé de la paire clé-valeur.
     * @param value La valeur associée à la clé.
     */
    public void addData(String key, String value) {
        // Trouver le nœud responsable de la clé à l'aide de l'algorithme Chord
        ChordNode responsibleNode = findSuccessor(key);
        // Stocker la paire clé-valeur sur le nœud responsable
        if (responsibleNode != null) {
            responsibleNode.addData(key, value);
        } else {
            // Gérer le cas où aucun nœud responsable n'est trouvé pour la clé donnée
            System.err.println("Aucun nœud responsable trouvé pour la clé : " + key);
        }
    }


    // Other methods...
}
