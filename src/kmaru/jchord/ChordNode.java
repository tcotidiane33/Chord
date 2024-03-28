package kmaru.jchord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChordNode {
	private String nodeId;
	private ChordKey nodeKey;
	private ChordNode predecessor;

	private ChordNode successor;
	private FingerTable fingerTable;
	private Map<String, String> data;
	private final int numOfNodes;
	private String nodeUrl;
	private static final int NUM_OF_NODES = 5;
    private Finger[] fingers;


    public ChordNode(String nodeId, String nodeUrl, int numOfNodes) {
		this.nodeId = nodeId;
		this.nodeKey = new ChordKey(nodeId);
		this.fingerTable = new FingerTable(this);
		this.nodeUrl = nodeUrl;
		this.create();
		this.data = new HashMap<>();
		this.numOfNodes = numOfNodes;
	}

	public ChordNode(String nodeId) {
		this.nodeId = nodeId;
		this.nodeKey = new ChordKey(nodeId);
		this.numOfNodes = 0;
		this.fingerTable = new FingerTable(this);
		this.create();
		this.data = new HashMap<>();
	}

    public void create() {
        predecessor = null;
        successor = this;
    }

    public ChordNode findSuccessor(String identifier) {
        return findSuccessor(new ChordKey(identifier));
    }

    public ChordNode findSuccessor(ChordKey key) {
        if (successor == null || key.isBetween(this.getNodeKey(), successor.getNodeKey())) {
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
	public ChordNode getSuccessor() {
		return successor;
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
	ChordNode getPredecessor() {
		return predecessor;
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

    public void stopNode() {
        System.out.println("Node stopped.");
    }

    public Map<String, String> getAllData() {
        return new HashMap<>(data);
    }

    public void addData(String key, String value) {
        data.put(key, value);
    }

    public String getData(String key) {
        return data.get(key);
    }

	public ChordKey getNodeKey() {
		return nodeKey;
	}

    public List<String> getFiles() {
        List<String> fileList = new ArrayList<>();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            fileList.add(entry.getKey());
        }
        return fileList;
    }

    /**
     * Définit le successeur de ce nœud.
     *
     * @param successor Le nœud successeur à définir.
     */
    public void setSuccessor(ChordNode successor) {
        this.successor = successor;
    }

    /**
     * Définit le prédécesseur de ce nœud.
     *
     * @param predecessor Le nœud prédécesseur à définir.
     */
    public void setPredecessor(ChordNode predecessor) {
        this.predecessor = predecessor;
    }

    /**
     * Obtient l'identifiant du nœud.
     *
     * @return L'identifiant du nœud.
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * Obtient la table de doigts du nœud.
     *
     * @return La table de doigts du nœud.
     */
    public Map<Object, Object> getFingerTable() {
        Map<Object, Object> fingerTableMap = new HashMap<>();
        
        for (int i = 0; i < fingers.length; i++) {
            Finger finger = fingers[i];
            if (finger != null) {
                // Ajoutez les détails du doigt à la table de doigts
                fingerTableMap.put("Finger " + i, finger);
            }
        }
        return fingerTableMap;
    }

    /**
     * Lit un fichier à partir du nœud spécifié.
     *
     * @param node     Le nœud à partir duquel lire le fichier.
     * @param fileName Le nom du fichier à lire.
     */
    public void readFile(ChordNode node, String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Traitez chaque ligne du fichier
                System.out.println(line); // Par exemple, imprimez chaque ligne
            }
        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier: " + e.getMessage());
        }
    }

    // Other methods...

    // Getter and setter methods...

    // Helper methods...

}
