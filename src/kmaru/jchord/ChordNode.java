package kmaru.jchord;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChordNode {

	String nodeId;
	ChordKey nodeKey;
	ChordNode predecessor;
	ChordNode successor;
	FingerTable fingerTable;
	Map<String, String> data; // Structure de données pour stocker les données
	private String nodeUrl;


	public ChordNode(String nodeId) {
		this.nodeId = nodeId;
		this.nodeUrl = nodeUrl;
		this.nodeKey = new ChordKey(nodeId);
		this.fingerTable = new FingerTable(this);
		this.create();
		this.data = new HashMap<>(); // Initialisation de la structure de données
	}
	// Ajouter cette méthode à la classe ChordNode
	public Map<String, String> getAllData() {
		return this.data;
	}


	// Méthode pour ajouter des données au nœud
	public void addData(String key, String value) {
		data.put(key, value);
	}

	// Méthode pour récupérer les données du nœud associées à une clé donnée
	public String getData(String key) {
		return data.get(key);
	}

	public ChordNode findSuccessor(String identifier) {
		ChordKey key = new ChordKey(identifier);
		return findSuccessor(key);
	}

	public ChordNode findSuccessor(ChordKey key) {
		if (this == successor) {
			return this;
		}

		if (key.isBetween(this.getNodeKey(), successor.getNodeKey()) || key.compareTo(successor.getNodeKey()) == 0) {
			return successor;
		} else {
			ChordNode node = closestPrecedingNode(key);
			if (node == this) {
				return successor.findSuccessor(key);
			}
			return node.findSuccessor(key);
		}
	}

	private ChordNode closestPrecedingNode(ChordKey key) {
		for (int i = Hash.KEY_LENGTH - 1; i >= 0; i--) {
			Finger finger = fingerTable.getFinger(i);
			ChordKey fingerKey = finger.getNode().getNodeKey();
			if (fingerKey.isBetween(this.getNodeKey(), key)) {
				return finger.getNode();
			}
		}
		return this;
	}

	public void create() {
		predecessor = null;
		successor = this;
	}

	public void join(ChordNode node) {
		predecessor = null;
		successor = node.findSuccessor(this.getNodeId());
	}

	public void stabilize() {
		ChordNode node = successor.getPredecessor();
		if (node != null) {
			ChordKey key = node.getNodeKey();
			if ((this == successor) || key.isBetween(this.getNodeKey(), successor.getNodeKey())) {
				successor = node;
			}
		}
		successor.notifyPredecessor(this);
	}

	private void notifyPredecessor(ChordNode node) {
		ChordKey key = node.getNodeKey();
		if (predecessor == null || key.isBetween(predecessor.getNodeKey(), this.getNodeKey())) {
			predecessor = node;
		}
	}

	public void fixFingers() {
		for (int i = 0; i < Hash.KEY_LENGTH; i++) {
			Finger finger = fingerTable.getFinger(i);
			ChordKey key = finger.getStart();
			finger.setNode(findSuccessor(key));
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ChordNode[");
		sb.append("ID=" + nodeId);
		sb.append(",KEY=" + nodeKey);
		sb.append("]");
		return sb.toString();
	}

	public void printFingerTable(PrintStream out) {
		out.println("=======================================================");
		out.println("FingerTable: " + this);
		out.println("-------------------------------------------------------");
		out.println("Predecessor: " + predecessor);
		out.println("Successor: " + successor);
		out.println("-------------------------------------------------------");
		for (int i = 0; i < Hash.KEY_LENGTH; i++) {
			Finger finger = fingerTable.getFinger(i);
			out.println(finger.getStart() + "\t" + finger.getNode());
		}
		out.println("=======================================================");
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public ChordKey getNodeKey() {
		return nodeKey;
	}

	public void setNodeKey(ChordKey nodeKey) {
		this.nodeKey = nodeKey;
	}

	public ChordNode getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(ChordNode predecessor) {
		this.predecessor = predecessor;
	}

	public ChordNode getSuccessor() {
		return successor;
	}

	public void setSuccessor(ChordNode successor) {
		this.successor = successor;
	}

	public FingerTable getFingerTable() {
		return fingerTable;
	}

	public void setFingerTable(FingerTable fingerTable) {
		this.fingerTable = fingerTable;
	}

	// Structure de données pour stocker les noms de fichiers sur le nœud
	private List<String> files;

	// Constructeur de la classe ChordNode
	public ChordNode() {
		// Initialisation de la liste des fichiers
		this.files = new ArrayList<>();
	}

	// Méthode pour ajouter un fichier au nœud
	public void addFile(String fileName) {
		this.files.add(fileName);
	}

	// Méthode pour obtenir la liste des fichiers sur le nœud
	public List<String> getFiles() {
		return this.files;
	}

	// Méthode pour lire un fichier à partir du nœud
	public String readFile(String fileName) {
		// Parcourir la liste des fichiers pour rechercher le fichier spécifié
		for (String file : files) {
			if (file.equals(fileName)) {
				// Si le fichier est trouvé, vous pouvez le lire ici
				// Par exemple, vous pouvez lire le contenu du fichier et le retourner
				return "Contenu du fichier " + fileName;
			}
		}
		// Si le fichier n'est pas trouvé, vous pouvez retourner un message indiquant qu'il n'existe pas
		return "Le fichier " + fileName + " n'existe pas sur ce nœud.";
	}

	public String getNodeUrl() {
		return nodeUrl;
	}

}
