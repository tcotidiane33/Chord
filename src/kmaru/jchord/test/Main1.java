package kmaru.jchord.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import kmaru.jchord.Chord;
import kmaru.jchord.ChordException;
import kmaru.jchord.ChordNode;
import kmaru.jchord.Hash;

public class Main1 {

    public static final String HASH_FUNCTION = "SHA-1";
    public static final int KEY_LENGTH = 160;
    public static final int NUM_OF_NODES = 5;
    public static Chord chord = new Chord();

    static {
        try {
            System.out.println("tape: >help ``__Show available commands__``");
            initializeChord();
        } catch (ChordException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;
        while (!(input = reader.readLine()).equalsIgnoreCase("exit")) {

            handleCommand(input);
        }
        reader.close();
        chord.stop();
    }

    private static void initializeChord() throws ChordException, UnknownHostException, MalformedURLException {
        Hash.setFunction(HASH_FUNCTION);
        Hash.setKeyLength(KEY_LENGTH);

        String host = InetAddress.getLocalHost().getHostAddress();
        int port = 9000;

        for (int i = 0; i < NUM_OF_NODES; i++) {
            URL url = null;
            try {
                url = new URL("http", host, port + i, "");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
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
                    readData(Integer.parseInt(tokens[1]), tokens[2]);
                    break;
                case "remove":
                    removeData(Integer.parseInt(tokens[1]), tokens[2]);
                    break;
                case "create":
                    createNode(Integer.parseInt(tokens[1]));
                    break;
                case "drop":
                    dropNode(Integer.parseInt(tokens[1]));
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

    private static void addData(int nodeId, String key, String value) {
        ChordNode node = getNode(nodeId);
        if (node != null) {
            node.addData(key, value);
            System.out.println("Data added successfully.");
        } else {
            System.out.println("Node not found!");
        }
    }

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

    private static void dropNode(int idx) {
        ChordNode node = getNode(idx);
        if (node != null) {
            chord.dropNode(idx);
            System.out.println("Node dropped successfully.");
        } else {
            System.out.println("Node not found!");
        }
    }

    private static void listNodes() {
        for (int i = 0; i < NUM_OF_NODES; i++) {
            ChordNode node = getNode(i);
            System.out.println("Node " + i + ": " + (node != null ? node.getSucc().getSuccessor() : "Not found"));
        }
    }

    private static ChordNode getNode(int idx) {
        if (idx >= 0 && idx < NUM_OF_NODES) {
            return chord.getNode(idx);
        } else {
            return null;
        }
    }

    private static URL urlBuilder(int idx) {
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
}
