package kmaru.jchord.test;

import kmaru.jchord.*;

import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main1 {

    public static final String HASH_FUNCTION = "SHA-1";
    public static final int KEY_LENGTH = 160;
    public static final int NUM_OF_NODES = 5;
    public static Chord chord = new Chord();

    static {
        try {
            initializeChord();
        } catch (ChordException | MalformedURLException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("tape >help<");

        List<NodeInfo> nodeInfos = getNodeInfoList();
        saveNodeInfoToJson(nodeInfos, "node_info.json");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;
        while (!(input = reader.readLine()).equalsIgnoreCase("exit")) {
            handleCommand(input);
        }
        reader.close();
        chord.stop();
    }


    private static List<NodeInfo> getNodeInfoList() {
        List<NodeInfo> nodeInfos = new ArrayList<>();
        // Parcourez vos nœuds Chord et récupérez leurs informations
        for (int i = 0; i < NUM_OF_NODES; i++) {
            ChordNode node = getNode(i);
            if (node != null) {
                // Obtenez l'ID du nœud et le nombre d'octets stockés sur ce nœud
                String nodeId = node.getNodeId();
                long numBytes = node.getNumBytesStored(); // Vous devez implémenter cette méthode dans ChordNode
                // Créez un objet NodeInfo et ajoutez-le à la liste
                NodeInfo nodeInfo = new NodeInfo(nodeId, numBytes);
                nodeInfos.add(nodeInfo);
            }
        }
        return nodeInfos;
    }

    private static void saveNodeInfoToJson(List<NodeInfo> nodeInfos, String fileName) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(nodeInfos, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void initializeChord() throws ChordException, UnknownHostException, MalformedURLException {
        Hash.setFunction(HASH_FUNCTION);
        Hash.setKeyLength(KEY_LENGTH);

        String host = InetAddress.getLocalHost().getHostAddress();
        int port = 9000;

        for (int i = 0; i < NUM_OF_NODES; i++) {
            URL url = new URL("http", host, port + i, "");
            chord.createNode(url.toString());
        }

        fixFingerTables();
    }

    private static void fixFingerTables() throws ChordException {
        for (int i = 0; i < NUM_OF_NODES; i++) {
            chord.getNode(i).fixFingers();
        }
    }

    private static void handleCommand(String input) {
        String[] tokens = input.split(" ");
        String command = tokens[0];
        try {
            switch (command) {
                case "add":
                    addData(Integer.parseInt(tokens[1]), tokens[2], tokens[3]);
                    break;
                case "read":
                    readDataFromNodeById(Integer.parseInt(tokens[1]), tokens[2]);
                    break;
                case "remove":
                    removeData(Integer.parseInt(tokens[1]), tokens[2]);
                    break;
                case "create":
                    createNode(Integer.parseInt(tokens[1]));
                    break;
                case "drop":
                    dropNode(getNode(Integer.parseInt(tokens[1])));
                    break;
                case "list":
                    listNodes();
                    break;
                case "exit":
                    break;
                case "help":
                    printHelp();
                    break;
                default:
                    System.out.println("Unknown command");
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid command format.");
        }
    }

    private static void readDataFromNodeById(int nodeId, String key) {
        String nodeIdString = String.valueOf(nodeId);
        ChordNode node = getNodeByNodeId(nodeIdString);
        if (node != null) {
            String data = node.readData(key);
            if (data != null) {
                System.out.println("Data on Node " + nodeId + ": " + data);
            } else {
                System.out.println("No data found on Node " + nodeId + " for key: " + key);
            }
        } else {
            System.out.println("Node " + nodeId + " not found!");
        }
    }

    private static void addData(int nodeId, String key, String value) {
        ChordNode node = getNode(nodeId);
        if (node != null) {
            node.addData(key, value);
            System.out.println("Data added successfully.");
        } else {
            System.out.println("Node not found!");
        }
    }

    // Les méthodes readData et removeData restent inchangées

    private static void createNode(int idx) {
        try {
            if (idx >= 0 && idx < NUM_OF_NODES) {
                URL url = urlBuilder(idx);
                chord.createNode(url.toString());
                System.out.println("Node created successfully.");
            } else {
                System.out.println("Invalid node ID.");
            }
        } catch (ChordException e) {
            System.out.println("Failed to create node.");
            e.printStackTrace();
        }
    }

    // La méthode dropNode reste inchangée

    static void listNodes() {
        for (int i = 0; i < NUM_OF_NODES; i++) {
            ChordNode node = getNode(i);
            if (node != null) {
                ChordNode successor = node.getSucc();
                System.out.println("Node " + i + ": " + (successor != null ? successor.getSuccessor() : "Not found"));
            }
        }
    }
    private static ChordNode getNodeByNodeId(String nodeId) {
        for (int i = 0; i < NUM_OF_NODES; i++) {
            ChordNode node = getNode(i);
            if (node != null && node.getNodeId().equals(nodeId)) {
                return node;
            }
        }
        return null;
    }


    // Les méthodes readData et removeData restent inchangées
    private static void readData(int nodeId, String key) {
        ChordNode node = getNode(nodeId);
        if (node != null) {
            String data = node.readData(key);
            if (data != null) {
                System.out.println("Value: " + data);
            } else {
                System.out.println("Key not found!");
            }
        } else {
            System.out.println("Node not found!");
        }
    }

    private static void removeData(int nodeId, String key) {
        ChordNode node = getNode(nodeId);
        if (node != null) {
            node.removeData(key);
            System.out.println("Data removed successfully.");
        } else {
            System.out.println("Node not found!");
        }
    }

    // La méthode dropNode reste inchangée
    private static void dropNode(ChordNode node) {
        if (node != null) {
            closeConnections(node);
            releaseResources(node);
            ChordNode pred = node.getPredecessor();
            ChordNode succ = node.getSuccessor();
            if (pred != null) {
                pred.setSuccessor(succ);
            }
            if (succ != null) {
                succ.setPredecessor(pred);
            }
            node.getFingerTable().clear();
            Map<String, String> nodeData = node.getAllData();
            for (Map.Entry<String, String> entry : nodeData.entrySet()) {
                chord.addData(entry.getKey(), entry.getValue());
            }
            chord.removeNode(node);
            System.out.println("Node dropped successfully.");
        } else {
            System.out.println("Node not found!");
        }
    }

    private static void closeConnections(ChordNode node) {
        // Logique pour fermer les connexions du nœud
        // Par exemple :
        // Si le nœud a un successeur, fermez la connexion avec le successeur
        ChordNode successor = node.getSucc();
        if (successor != null) {
            // Ici, vous pouvez implémenter la logique pour fermer la connexion avec le successeur
            // Par exemple, si vous avez une classe de gestion des connexions, vous pouvez appeler une méthode de fermeture de connexion
            // Par exemple : ConnectionManager.closeConnection(node, successor);
            System.out.println("Fermeture de la connexion avec le successeur.");
        }
        // Si le nœud a un prédécesseur, fermez la connexion avec le prédécesseur
        ChordNode predecessor = node.getPredecessor();
        if (predecessor != null) {
            // Ici, vous pouvez implémenter la logique pour fermer la connexion avec le prédécesseur
            // Par exemple : ConnectionManager.closeConnection(node, predecessor);
            System.out.println("Fermeture de la connexion avec le prédécesseur.");
        }
        // Autres fermetures de connexions nécessaires selon votre architecture
    }

    private static void releaseResources(ChordNode node) {
        // Logique pour libérer les ressources associées au nœud
        // Par exemple :
        // Si le nœud a des fichiers ouverts, fermez-les
        List<String> files = node.getFiles();
        if (files != null && !files.isEmpty()) {
            for (String file : files) {
                // Ici, vous pouvez implémenter la logique pour fermer chaque fichier
                // Par exemple : FileManager.closeFile(file);
                System.out.println("Fermeture du fichier : " + file);
            }
        }
        // Autres libérations de ressources nécessaires selon votre architecture
    }



    // Les méthodes getNode et urlBuilder restent inchangées
    private static ChordNode getNode(int idx) {
        if (idx >= 0 && idx < NUM_OF_NODES) {
            ChordNode node = chord.getNode(idx);
            if (node != null && node.getSucc() != null) {
                return node;
            }
        }
        return null;
    }

    static URL urlBuilder(int idx) {
        String host;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
            int port = 9000 + idx;
            return new URL("http", host, port, "");
        } catch (Exception e) {
            System.out.println("Failed to build URL.");
            e.printStackTrace();
            return null;
        }
    }


    private static void printHelp() {
        System.out.println("====== Available commands:  ======");
        System.out.println("====== add <nodeId> <key> <value>: Add data to a specific node.  ======");
        System.out.println("====== read <nodeId> <key>: Read data from a specific node.  ======");
        System.out.println("====== remove <nodeId> <key>: Remove data from a specific node.  ======");
        System.out.println("====== create <nodeId>: Create a new node with the specified ID.  ======");
        System.out.println("====== drop <nodeId>: Drop a node with the specified ID.  ======");
        System.out.println("====== list: List all nodes.  ======");
        System.out.println("====== exit: Exit the program.  ======");
        System.out.println("====== help: Show available commands.  ======");
    }
     private static List<Map<String, String>> partitionData() throws IOException {
        List<Map<String, String>> partitions = new ArrayList<>();
        File file = new File("Main.java"); // Chemin vers le fichier Main.java dans le même répertoire
        if (file.exists()) {
            Map<String, String> partition = new HashMap<>();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    // Traitement de chaque ligne de votre fichier Main.java
                    // Par exemple, vous pouvez diviser la ligne en clé et en valeur
                    // et les ajouter à votre partition
                    // Ici, nous ajoutons simplement la ligne complète comme clé et une chaîne vide comme valeur
                    partition.put(line, "");
                }
            }
            partitions.add(partition);
        } else {
            System.out.println("Le fichier Main.java n'existe pas dans le répertoire actuel.");
        }
        return partitions;
    }

}
