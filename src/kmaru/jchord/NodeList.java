package kmaru.jchord;

import java.util.ArrayList;
import java.util.List;

public class NodeList {
    private static List<ChordNode> nodes = new ArrayList<>();

    // Méthode statique pour ajouter un nœud à la liste
    public static void addNode(ChordNode node) {
        nodes.add(node);
    }

    // Méthode statique pour supprimer un nœud de la liste
    public static void removeNode(ChordNode node) {
        nodes.remove(node);
    }

    // Méthode statique pour obtenir la liste des noms de nœuds
    public static List<String> getNodeList() {
        List<String> nodeList = new ArrayList<>();
        for (ChordNode node : nodes) {
            nodeList.add(node.getNodeId());
        }
        return nodeList;
    }

    // Autres méthodes utiles à implémenter selon les besoins
}
