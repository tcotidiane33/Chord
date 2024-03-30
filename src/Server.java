import bdgl.jchord.Chord;
import bdgl.jchord.Hash;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Server {
    private static final Chord chord = new Chord();
    private static final String serverAddress = "192.168.1.100";
    private static final int serverPort = 8080;
    private static List<String> clientAddresses = new ArrayList<>();
    private static List<Integer> clientPorts = new ArrayList<>();
    private static List<String> nodeAddresses = new ArrayList<>();
    private static List<Integer> nodePorts = new ArrayList<>();

    public static void main(String[] args) {
        initializeChord();

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Serveur en attente de connexions sur le port " + serverPort + "...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientAddress = clientSocket.getInetAddress().getHostAddress();
                int clientPort = clientSocket.getPort();
                clientAddresses.add(clientAddress);
                clientPorts.add(clientPort);
                System.out.println("Connexion entrante depuis " + clientAddress + ":" + clientPort);

                try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), UTF_8));
                     PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), UTF_8), true)) {
                    String request;
                    while ((request = in.readLine()) != null) {
                        System.out.println("Requête du client : " + request);
                        logOperation("Client " + clientAddress + ":" + clientPort + " - " + request);
                        String response = processRequest(request);
                        out.println(response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initializeChord() {
        Hash.setFunction(Hash.HashFunction.SHA1);
        Hash.setKeyLength(160);
        String host = serverAddress;
        int port = 9000;
        final int NUM_OF_NODES = 5;
        try {
            for (int i = 0; i < NUM_OF_NODES; i++) {
                URL url = new URL("http", host, port + i, "");
                chord.createNode(url.toString());
                nodeAddresses.add(url.getHost());
                nodePorts.add(url.getPort());
                System.out.println("Nouveau nœud créé à l'adresse " + url.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private static String processRequest(String request) {
        String[] tokens = request.split("\\s+");
        if (tokens.length == 0) {
            return "Commande invalide.";
        }

        String command = tokens[0].toLowerCase();
        switch (command) {
            case "create":
                System.out.println("Nouveau nœud créé.");
                return createNode(tokens);
            case "drop":
                System.out.println("Nœud supprimé.");
                return dropNode(tokens);
            case "list":
                System.out.println("Liste des nœuds.");
                return listNodes(tokens);
            case "read":
                System.out.println("Lecture des données d'un nœud.");
                return readNode(tokens);
            case "load":
                System.out.println("Chargement de nouvelles données.");
                return loadNodes(tokens);
            case "partition":
                System.out.println("Affichage des partitions de données.");
                return displayPartitions(tokens);
            case "help":
                return getHelpMessage();
            case "exit":
                System.out.println("Bye Bye <3");
                System.exit(0);
            default:
                return "Commande inconnue.";
        }
    }

    private static String getHelpMessage() {
        String helpMessage = "====== Available commands ======\n" +
                "=================================\n" +
                "create: Create a new node.\n" +
                "=================================\n" +
                "drop: Drop a node.\n" +
                "=================================\n" +
                "list: List all nodes.\n" +
                "=================================\n" +
                "read: Read data from a node.\n" +
                "=================================\n" +
                "load: Load data onto nodes.\n" +
                "=================================\n" +
                "partition: Display data partitions.\n" +
                "=================================\n" +
                "help: Show available commands.\n" +
                "=================================\n" +
                "exit: Exit the program.\n" +
                "=================================";
        return helpMessage;
    }

    private static void logOperation(String operation) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("server_log.txt", true))) {
            writer.println(operation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String createNode(String[] tokens) {
        if (tokens.length < 2) {
            return "La commande create nécessite un identifiant de nœud.";
        }

        String nodeId = tokens[1];
        try {
            // Logique pour créer un nouveau nœud avec l'identifiant nodeId
            return "Nouveau nœud créé avec l'identifiant " + nodeId + ".";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la création du nœud avec l'identifiant " + nodeId + ".";
        }
    }

    private static String dropNode(String[] tokens) {
        try {
            // Logique pour supprimer un nœud
            return "Nœud supprimé.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la suppression du nœud.";
        }
    }

    private static String listNodes(String[] tokens) {
        try {
            // Logique pour lister tous les nœuds
            return "Liste des nœuds.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la récupération de la liste des nœuds.";
        }
    }

    private static String readNode(String[] tokens) {
        try {
            // Logique pour lire les données d'un nœud
            return "Lecture des données d'un nœud.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la lecture des données du nœud.";
        }
    }

    private static String loadNodes(String[] tokens) {
        try {
            // Logique pour charger de nouvelles données sur les nœuds
            return "Chargement de nouvelles données.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors du chargement de nouvelles données.";
        }
    }

    private static String displayPartitions(String[] tokens) {
        try {
            // Logique pour afficher les partitions de données
            return "Affichage des partitions de données.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de l'affichage des partitions de données.";
        }
    }
}
