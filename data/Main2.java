package kmaru.jchord.test;

import kmaru.jchord.Chord;
import kmaru.jchord.ChordException;
import kmaru.jchord.ChordNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import static java.awt.geom.Arc2D.CHORD;
import static java.lang.String.format;
import static kmaru.jchord.ChordNetwork.*;

public class Main2 {

    public static final String HASH_FUNCTION = "SHA-1";

    public static final int KEY_LENGTH = 160;
    public static final int NUM_NODES = 5;

    public static void main(String[] args) throws Exception {
        initializeChord();
        loadAndDisplayData();
        handleUserCommands();
    }

    public static void initializeChord() throws ChordException {
        for (int i = 0; i < NUM_NODES; i++) {
            int port = getStartPort() + i;
            try {
                ChordNode node = CHORD_NETWORK.createNode(GET_JOIN_PORT, CHORD_NETWORK.createKey(HASH_FUNCTION, KEY_LENGTH, "localhost:" + port));                addNode(node);
                System.out.println(format("Node [%d] created at localhost:%d", i, port));
            } catch (IllegalArgumentException e) {
                throw new ChordException("Error initializing Chord node: " + e.getMessage());
            }
        }

        System.out.println("Network initialized");
    }

    public static void loadAndDisplayData() throws Exception {
        addData(CHORD, "file1", "sample1");
        addData(CHORD, "file2", "sample2");
        addData(CHORD, "file3", "sample3");
        addData(CHORD, "file4", "sample4");

        displayPartitions();
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

    private static ChordNode getNode(int nodeId) {
        if (nodeId < 0 || nodeId >= NUM_NODES) {
            throw new IllegalArgumentException("Invalid node ID.");
        }

        ChordNode node = CHORD.getNode(nodeId);
        if (node == null) {
            throw new IllegalStateException("Node " + nodeId + " not found.");
        }

        return node;
    }


    public static ChordNode getNode(Chord chord, int nodeId) {
        return chord.getNodes().stream()
                .filter(node -> node.getNodeId() == nodeId)
                .findFirst()
                .orElse(null);
    }

    public static void displayPartitions() {
        System.out.println("=== Data Partitions ===");
        stream(CHORD.getNodes()).forEach(n -> {
            System.out.println("Node " + n.getNodeId() + " data: ");
            n.getAllData().forEach((k, v) -> System.out.println("\t" + k + " -> " + v));
        });
    }

    public static void handleUserCommands() throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String input;
            while (!(input = reader.readLine()).equalsIgnoreCase("exit")) {
                String[] tokens = input.trim().toLowerCase().split("\\s+");
                if (tokens.length < 1) continue;

                switch (tokens[0]) {
                    case "create":
                        if (tokens.length < 2) {
                            System.out.println("Please specify the ID of the new node.");
                            continue;
                        }

                        int idx = Integer.parseInt(tokens[1]);
                        if (idx < 0 || idx >= NUM_NODES) {
                            System.out.println("Invalid node ID.");
                            continue;
                        }

                        Chord chord = CHORD;
                        ChordNode newNode = ChordNode.createNode(chord, chord.createKey(HASH_FUNCTION, KEY_LENGTH,
                                format("localhost:%d", START_PORT + idx)));

                        addNode(newNode);
                        System.out.println("Node " + idx + " created.");
                        break;

                    case "drop":
                        dropNode();
                        break;
                    case "display":
                        displayPartitions();
                        break;

                    case "list":
                        listNodes();
                        break;

                    case "read":
                        readNode(tokens);
                        break;

                    case "load":
                        addData(CHORD, "file1", "sample1");
                        addData(CHORD, "file2", "sample2");
                        addData(CHORD, "file3", "sample3");
                        addData(CHORD, "file4", "sample4");

                        displayPartitions();
                        break;

                    default:
                        System.out.println("Unknown command. Use 'CREATE', 'DROP', 'LIST', 'READ', 'LOAD', 'DISPLAY', or 'EXIT'.");
                }
            }
        }
    }


    public static void readNode(String[] tokens) {
        if (tokens.length < 3) {
            System.out.println("Please specify the index of the node and key of the file.");
            return;
        }

        int idx = Integer.parseInt(tokens[1]);
        if (idx < 0 || idx >= NUM_NODES) {
            System.out.println("Invalid node ID.");
            return;
        }

        ChordNode node = CHORD.getNode(idx);
        if (node != null) {
            String result = node.readData(tokens[2]);
            System.out.println("Content of " + tokens[2] + " on Node " + idx + ": " + (result != null ? result : "Not Found"));
        }
        else {
            System.out.println("Node " + idx + " not found.");
        }
    }

    public static void dropNode() {
        System.out.println("Enter the ID of the node to be dropped:");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String input = reader.readLine().trim();
            int idx = Integer.parseInt(input);

            if (idx < 0 || idx >= NUM_NODES) {
                System.out.println("Invalid node ID.");
                return;
            }

            ChordNode node = CHORD.getNode(idx);
            if (node != null) {
                removeNode(node);
                System.out.println("Node " + idx + " dropped.");
            }
            else {
                System.out.println("Node " + idx + " not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void listNodes() {
        System.out.println("=== Node List ===");
        stream(CHORD.getNodes()).forEach(node -> System.out.println(node.getNodeId()));
    }

}