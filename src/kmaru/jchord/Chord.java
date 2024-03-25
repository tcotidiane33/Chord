package kmaru.jchord;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class Chord {

	private List<ChordNode> nodeList = new ArrayList<>();
	private SortedMap<ChordKey, ChordNode> sortedNodeMap = new TreeMap<>();
	private Object[] sortedKeyArray;
	private List<ChordNode> nodes = new ArrayList<>();

	public void createNode(String nodeId) throws ChordException {
		ChordNode node = new ChordNode(nodeId);
		nodeList.add(node);

		if (sortedNodeMap.get(node.getNodeKey()) != null) {
			throw new ChordException("Duplicated Key: " + node);
		}

		sortedNodeMap.put(node.getNodeKey(), node);
		nodes.add(node); // Ajoutez le nouveau nœud à la liste `nodes`
	}

	public ChordNode getNode(int i) {
		return nodeList.get(i);
	}

	public ChordNode getSortedNode(int i) {
		if (sortedKeyArray == null) {
			sortedKeyArray = sortedNodeMap.keySet().toArray();
		}
		return sortedNodeMap.get(sortedKeyArray[i]);
	}

	public ChordNode findSuccessor(String key) {
		ChordKey chordKey = new ChordKey(key);
		return findSuccessor(chordKey);
	}

	public void dropNode(int index) {
		if (index >= 0 && index < nodeList.size()) {
			nodeList.remove(index);
			System.out.println("Node at index " + index + " removed successfully.");
		} else {
			System.out.println("Invalid node index.");
		}
	}

	public ChordNode findSuccessor(ChordKey key) {
		if (sortedNodeMap.isEmpty()) {
			return null;
		}

		if (key.compareTo(sortedNodeMap.lastKey()) > 0 || key.compareTo(sortedNodeMap.firstKey()) < 0) {
			return sortedNodeMap.get(sortedNodeMap.firstKey());
		}

		ChordNode successor = sortedNodeMap.get(sortedNodeMap.tailMap(key).firstKey());
		return successor != null ? successor : sortedNodeMap.get(sortedNodeMap.firstKey());
	}

	public int size() {
		return nodeList.size();
	}

	public void stop() {
		// Arrêter tous les nœuds du réseau
		for (ChordNode node : nodes) {
			node.stopNode();
		}

		// Fermeture des connexions réseau
		// Exemple :
		// for (Connection connection : openConnections) {
		//     connection.close();
		// }

		// Libération de la mémoire ou nettoyage des structures de données
		// Exemple :
		// temporaryData.clear();

		// Fermeture des fichiers ouverts
		// Exemple :
		// for (File file : openFiles) {
		//     file.close();
		// }

		// Arrêt des threads ou tâches en cours
		// Exemple :
		// for (Thread thread : runningThreads) {
		//     thread.interrupt();
		// }

		// Afficher un message de confirmation
		System.out.println("Le système Chord a été arrêté avec succès.");
	}

}
