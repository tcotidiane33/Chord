package kmaru.jchord.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.Map;

import kmaru.jchord.Chord;
import kmaru.jchord.ChordException;
import kmaru.jchord.ChordNode;
import kmaru.jchord.Hash;

public class Main1 {

    public static final String HASH_FUNCTION = "SHA-1";
    public static final int KEY_LENGTH = 160;
    public static final int NUM_OF_NODES = 5;

    public static void main(String[] args) throws Exception {
        PrintStream out = new PrintStream("result.log");

        String host = InetAddress.getLocalHost().getHostAddress();
        int port = 9000;

        Hash.setFunction(HASH_FUNCTION);
        Hash.setKeyLength(KEY_LENGTH);

        Chord chord = new Chord();
        for (int i = 0; i < NUM_OF_NODES; i++) {
            URL url = new URL("http", host, port + i, "");
            try {
                chord.createNode(url.toString());
            } catch (ChordException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        out.println(NUM_OF_NODES + " nodes are created.");

        for (int i = 0; i < NUM_OF_NODES; i++) {
            ChordNode node = chord.getSortedNode(i);
            out.println(node);
        }

        for (int i = 0; i < NUM_OF_NODES; i++) {
            ChordNode node = chord.getNode(i);
            if (node != null) {
                // Afficher les données du nœud
                System.out.println("Content of node " + i + ": ");
                Map<String, String> allData = node.getAllData();
                for (Map.Entry<String, String> entry : allData.entrySet()) {
                    System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
                }
            } else {
                System.out.println("Node " + i + " not found!");
            }
        }

        out.println("Chord ring is established.");

        for (int i = 0; i < NUM_OF_NODES; i++) {
            ChordNode node = chord.getNode(i);
            node.fixFingers();
        }
        out.println("Finger Tables are fixed.");

        for (int i = 0; i < NUM_OF_NODES; i++) {
            ChordNode node = chord.getSortedNode(i);
            node.printFingerTable(out);
        }

        long start = System.currentTimeMillis();

        // Interaction avec les nœuds du réseau
        // Affichage des identifiants des nœuds
        System.out.println("Nodes in the network:");
        for (int i = 0; i < NUM_OF_NODES; i++) {
            System.out.println("Node " + i + ": " + chord.getNode(i).getNodeId());
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println();
            System.out.print("Enter node ID to retrieve its content (or type 'exit' to quit): ");
            String input = reader.readLine();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
            try {
                int nodeId = Integer.parseInt(input);
                if (nodeId >= 0 && nodeId < NUM_OF_NODES) {
                    ChordNode node = chord.getNode(nodeId);
                    if (node != null) {
                        // Afficher les données du nœud
                        System.out.println("Content of node " + nodeId + ": ");
                        for (String key : node.getData().keySet()) {
                            System.out.println("Key: " + key + ", Value: " + node.getData(key));
                        }

                    } else {
                        System.out.println("Node not found!");
                    }
                } else {
                    System.out.println("Invalid node ID. Please enter a valid node ID.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid node ID.");
            }
        }
        reader.close();

        long end = System.currentTimeMillis();
        int interval = (int) (end - start);
        System.out.printf("Elapsed Time : %d.%d\n", interval / 1000, interval % 1000);
    }
}
